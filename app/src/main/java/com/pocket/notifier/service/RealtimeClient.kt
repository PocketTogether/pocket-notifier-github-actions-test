package com.pocket.notifier.service

import android.content.Context
import com.pocket.notifier.config.Config
import com.pocket.notifier.notification.NotificationHelper
import com.pocket.notifier.store.MessageStore
import com.pocket.notifier.store.StatusStore
import com.pocket.notifier.store.StoredMessage
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * RealtimeClient — PocketBase SSE 实时客户端
 *
 * 职责：
 * - 建立 GET /api/realtime 长连接
 * - 解析 SSE 事件流（PB_CONNECT + 实际事件）
 * - 在 PB_CONNECT 后发送 POST /api/realtime 设置订阅
 * - 对 action = "create" 的事件，转换为 StoredMessage，写入 MessageStore 并发通知
 * - GET / POST 成功与否写入 StatusStore
 *
 * 说明：
 * - 使用 OkHttp 手动解析 text/event-stream
 * - 单次会话时长由 Config.REALTIME_SESSION_SECONDS 控制（例如 120 秒）
 * - 会话结束或异常后自动重连（带少量延迟）
 */
class RealtimeClient(
    private val context: Context,
    private val client: OkHttpClient, // 用于 POST /api/realtime（订阅） 
    private val clientForSSE: OkHttpClient, // 用于 GET /api/realtime（长连接）
    private val scope: CoroutineScope
) {

    private var job: Job? = null

    fun start() {
        if (job == null || job?.isCancelled == true) {
            job = scope.launch { realtimeLoop() }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }

    private suspend fun realtimeLoop() {
        while (scope.isActive) {
            try {
                connectOnce()
                // 只要本次会话建立并正常结束，就认为是成功
                StatusStore.setLastStatus(context, true)
            } catch (e: Exception) {
                StatusStore.setLastStatus(context, false)
                NotificationHelper.sendError(
                    context,
                    "Realtime error: ${e::class.java.simpleName}: ${e.message}"
                )
                // 避免疯狂重连，稍微等一下
                delay(5_000)
            }
        }
    }

    /**
    * 单次 SSE 会话：
    * - 建立 GET /api/realtime 长连接
    * - 持续读取 SSE 行直到：
    *      1) 服务器断开（EOF）
    *      2) 发生异常
    *      3) OkHttp 的 readTimeout 到时（我们用它来主动断开）
    *      4) scope 被取消
    *
    * ⭐ 关键机制说明：
    * - source.readUtf8Line() 是“强阻塞 IO”，不会响应协程取消，也不会被 withTimeout 中断
    * - 因此真正能主动中断它的只有 OkHttp 的 readTimeout
    * - 当 readTimeout 到时，会抛出 SocketTimeoutException
    * - 我们把这个异常视为“主动断开”，而不是错误
    */
    private suspend fun connectOnce() {
        // NotificationHelper.sendError(context, "SSE: 开始建立连接")

        val request = Request.Builder()
            .url(Config.REALTIME_URL)
            .get()
            .build()

        // // 🧐 测试：记录本次 SSE 会话的开始时间
        // val connectStart = System.currentTimeMillis()

        try {
            clientForSSE.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    // NotificationHelper.sendError(context, "SSE: 连接失败 HTTP ${response.code}")
                    throw IOException("HTTP ${response.code} on realtime connect")
                }

                // NotificationHelper.sendError(context, "SSE: 连接成功，开始读取事件")

                val source = response.body!!.source()

                var currentEvent: String? = null
                var dataBuilder = StringBuilder()

                /**
                * ⭐ 循环条件：
                * - scope.isActive：允许外部主动取消（例如服务停止）
                * - readUtf8Line()：阻塞等待服务器发送数据
                *
                * ⚠ 注意：
                * readUtf8Line() 是强阻塞，不会因为协程取消而自动中断，
                * 所以真正的“主动断开”依赖 OkHttp 的 readTimeout。
                */
                while (scope.isActive) {

                    /**
                    * ⭐ readUtf8Line() 行为：
                    * - 如果服务器发送了一行 → 返回该行
                    * - 如果服务器断开 → 返回 null（EOF）
                    * - 如果服务器长时间不发数据 → 一直阻塞，直到 readTimeout 触发
                    */
                    val line = source.readUtf8Line() ?: break  // EOF → 正常断开

                    // ⭐ 空行表示一个 SSE 事件结束
                    if (line.isEmpty()) {
                        val data = dataBuilder.toString().trim()
                        if (data.isNotEmpty()) {
                            // NotificationHelper.sendError(context, "SSE: 收到事件 → $currentEvent")
                            handleEvent(currentEvent, data)
                        }
                        currentEvent = null
                        dataBuilder = StringBuilder()
                        continue
                    }

                    // ⭐ 解析 event: 和 data:
                    when {
                        line.startsWith("event:") -> {
                            currentEvent = line.removePrefix("event:").trim()
                        // NotificationHelper.sendError(context, "SSE: event = $currentEvent")
                        }

                        line.startsWith("data:") -> {
                            if (dataBuilder.isNotEmpty()) dataBuilder.append('\n')
                            dataBuilder.append(line.removePrefix("data:").trim())
                        }
                    }
                }

                // NotificationHelper.sendError(context, "SSE: 会话结束（正常 EOF 或 scope 取消）")
            }

        } catch (e: SocketTimeoutException) {
            /**
            * ⭐ 关键点：主动断开
            *
            * 当 OkHttp 的 readTimeout 到时：
            * - readUtf8Line() 会被强制中断
            * - OkHttp 会抛出 SocketTimeoutException
            *
            * 这正是我们用来模拟“浏览器每 60 秒主动断开”的机制。
            */
            // NotificationHelper.sendError(context, "SSE: 会话超时 → 主动断开（正常）")

        } catch (e: Exception) {
            // NotificationHelper.sendError(context, "SSE: 异常断开 → ${e.message}")
            throw e
        } finally {
            // // 🧐 测试：记录结束时间并计算秒数 
            // val connectEnd = System.currentTimeMillis() 
            // val durationSec = (connectEnd - connectStart) / 1000
            // NotificationHelper.sendError(context, "SSE: 持续时间 ${durationSec} 秒")
        }

    }

    /**
     * 处理单个 SSE 事件
     *
     * - event == "PB_CONNECT" → 解析 clientId → POST /api/realtime 设置订阅
     * - 其他事件：如果 data 中有 action == "create" → 解析 record → 写入本地并发通知
     */
    private suspend fun handleEvent(event: String?, data: String) {
        if (event == "PB_CONNECT") {
            val json = JSONObject(data)
            val clientId = json.getString("clientId")
            sendSubscription(clientId)
            // 订阅成功也视为成功
            StatusStore.setLastStatus(context, true)
            return
        }

        // 其他事件：尝试解析 action
        val json = JSONObject(data)
        if (!json.has("action")) return

        val action = json.getString("action")
        if (action != "create") return

        val record = json.getJSONObject("record")
        val expand = record.getJSONObject("expand").getJSONObject("author")

        val message = StoredMessage(
            id = record.getString("id"),
            created = record.getString("created"),
            content = record.getString("content"),
            authorName = expand.optString("name", ""),
            authorUsername = expand.optString("username", "")
        )

        // 避免重复：如果本地已经有该 id，则忽略
        val stored = MessageStore.getMessages(context)
        val exists = stored.any { it.id == message.id }
        if (!exists) {
            // 写入本地（保持与轮询一致：升序追加）
            MessageStore.addMessages(context, listOf(message))
            // 单条消息通知
            NotificationHelper.sendNewMessage(context, message)
        }
    }

    /**
     * 在 PB_CONNECT 后调用：
     * - POST /api/realtime
     * - Body:
     *   {
     *     "clientId": "...",
     *     "subscriptions": [
     *       Config.REALTIME_SUBSCRIPTION
     *     ]
     *   }
     */
    private suspend fun sendSubscription(clientId: String) {
        val bodyJson = JSONObject().apply {
            put("clientId", clientId)
            put(
                "subscriptions",
                JSONArray().apply {
                    put(Config.REALTIME_SUBSCRIPTION)
                }
            )
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = bodyJson.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(Config.REALTIME_URL)
            .post(body)
            .build()

        withContext(Dispatchers.IO) {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("HTTP ${response.code} on realtime subscribe")
                }
            }
        }
    }
}

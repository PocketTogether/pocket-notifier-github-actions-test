package com.pocket.notifier.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.pocket.notifier.R
import com.pocket.notifier.config.Config
import com.pocket.notifier.notification.NotificationHelper
import com.pocket.notifier.store.StatusStore
import com.pocket.notifier.store.MessageStore
import com.pocket.notifier.store.StoredMessage
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class PollingService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(Config.REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(Config.REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(Config.REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        // ⭐
        NotificationHelper.initChannels(this)

        startForeground(1, createForegroundNotification())
        startPollingLoop()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    /** 前台服务通知（常驻） */
    private fun createForegroundNotification(): Notification {
        return NotificationCompat.Builder(this, NotificationHelper.CHANNEL_FOREGROUND)
            .setContentTitle("Notifier is running in the background.")
            .setSmallIcon(R.drawable.ic_notify)
            .setOngoing(true)
            .build()
    }

    /** 轮询循环 */
    private fun startPollingLoop() {
        scope.launch {
            while (isActive) {
                performRequest()
                delay(Config.POLLING_INTERVAL_SECONDS * 1000)
            }
        }
    }

    // 广播以实现主图实时更新
    private fun sendStatusBroadcast() {
        // 📌 创建一个 Intent，action 名称是自定义的事件标识
        // “NOTIFIER_STATUS_UPDATED” 表示：轮询状态（成功/失败）已经更新
        val intent = Intent("NOTIFIER_STATUS_UPDATED")

        // 📌 发送广播（Broadcast）
        // 任何注册了这个 action 的组件（例如 MainActivity）都会立即收到通知
        // 这就是安卓世界里最轻量、最实时、最省电的“事件通知机制”
        sendBroadcast(intent)
    }

    /** 执行一次 HTTP 轮询请求 */
    private fun performRequest() {

        // 构建 GET 请求（URL 从 Config 读取，集中配置）
        val request = Request.Builder()
            .url(Config.REQUEST_URL)
            .get()
            .build()

        try {
            // 同步执行 HTTP 请求（在 IO 线程中，不阻塞主线程）
            client.newCall(request).execute().use { response ->

                // 非 2xx 状态码 → 视为请求失败
                if (!response.isSuccessful) {
                    StatusStore.setLastStatus(this@PollingService, false)
                    NotificationHelper.sendError(this, "HTTP ${response.code}")
                    return
                }

                // 读取响应 body（string() 会一次性读取并关闭流）
                val body = response.body?.string() ?: return

                // 解析 PocketBase 标准 JSON 结构
                val json = JSONObject(body)
                val items = json.getJSONArray("items")
                val perPage = json.getInt("perPage")   // 用于通知显示

                // 将 JSON 转换为 StoredMessage 列表
                val received = mutableListOf<StoredMessage>()
                for (i in 0 until items.length()) {
                    val obj = items.getJSONObject(i)
                    val expand = obj.getJSONObject("expand").getJSONObject("author")

                    received.add(
                        StoredMessage(
                            id = obj.getString("id"),
                            created = obj.getString("created"),
                            content = obj.getString("content"),
                            authorName = expand.getString("name"),
                            authorUsername = expand.getString("username")
                        )
                    )
                }

                // PocketBase 返回的 items 是按 created DESC 排序（最新在前）
                // 本地存储是 ASC（旧 → 新）
                val stored = MessageStore.getMessages(this)
                val storedIds = stored.map { it.id }.toSet()   // 使用 Set 加速查重

                // 过滤出本地不存在的新消息
                val newMessages = received.filter { it.id !in storedIds }

                // 如果有新消息 → 发通知 + 写入本地
                if (newMessages.isNotEmpty()) {

                    // 通知使用 DESC 顺序（最新在前）
                    NotificationHelper.sendNewMessages(this, newMessages, perPage)

                    // 写入本地前按 created 升序排序（保持存储一致性）
                    MessageStore.addMessages(this, newMessages.sortedBy { it.created })
                }

                // 请求成功（包括“无新消息”这种正常情况）
                StatusStore.setLastStatus(this@PollingService, true)
            }

        } catch (e: Exception) {

            // 捕获所有异常 → 不崩溃 → 标记失败
            StatusStore.setLastStatus(this@PollingService, false)

            // 发送错误通知（包含异常类型 + message，便于调试）
            NotificationHelper.sendError(this, "${e::class.java.simpleName}: ${e.message}")
        }

        // 无论成功失败都广播状态 → UI 可刷新
        sendStatusBroadcast()
    }

}
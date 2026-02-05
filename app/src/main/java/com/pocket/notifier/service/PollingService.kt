package com.pocket.notifier.service

/**
 * PollingService — 后台轮询服务（ForegroundService）
 *
 * 功能：
 * - 每 60 秒轮询一次 HTTP 请求
 * - 使用 OkHttp + 协程
 * - 请求超时 10 秒
 * - 成功/失败写入 SharedPreferences（StatusStore）
 * - 每次轮询后发送通知（NotificationHelper）
 * - Android 13+ 兼容的前台服务类型声明
 */

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.pocket.notifier.R
import com.pocket.notifier.config.Config
import com.pocket.notifier.notification.NotificationHelper
import com.pocket.notifier.store.StatusStore
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
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
            .setContentTitle("Notifier 正在后台运行.")
            .setContentText("正在轮询服务器…")
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

    /** 执行 HTTP 请求 */
    private fun performRequest() {
        val request = Request.Builder()
            .url(Config.REQUEST_URL)
            .get()
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val code = response.code
                val success = response.isSuccessful

                StatusStore.setLastStatus(this@PollingService, success)

                val msg = "HTTP $code"

                NotificationHelper.sendPollingNotification(
                    context = this@PollingService,
                    success = success,
                    message = msg
                )
            }
        } catch (e: Exception) {
            StatusStore.setLastStatus(this@PollingService, false)

            val msg = "${e::class.java.simpleName}: ${e.message}"

            NotificationHelper.sendPollingNotification(
                context = this@PollingService,
                success = false,
                message = msg
            )
        }
        
        // 广播以实现主图实时更新
        sendStatusBroadcast()
    }

}
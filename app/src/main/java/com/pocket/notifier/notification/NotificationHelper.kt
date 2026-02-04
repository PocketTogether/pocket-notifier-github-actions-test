package com.pocket.notifier.notification

/**
 * NotificationHelper — 通知系统
 *
 * 功能：
 * - 创建通知渠道（Android 8+ 必须）
 * - ForegroundService 专用渠道
 * - 轮询通知专用渠道
 * - 每次轮询后发送成功/失败通知（带时间戳）
 */

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.pocket.notifier.R
import java.text.SimpleDateFormat
import java.util.*

object NotificationHelper {

    /** ForegroundService 常驻通知渠道 */
    const val CHANNEL_FOREGROUND = "notifier_foreground"

    /** 轮询通知渠道 */
    const val CHANNEL_POLLING = "notifier_polling"

    /** 初始化通知渠道（在 Application 或 Service 中调用一次） */
    fun initChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)

            val foreground = NotificationChannel(
                CHANNEL_FOREGROUND,
                "后台运行通知",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "用于保持 Notifier 在后台运行的前台服务通知"
            }

            val polling = NotificationChannel(
                CHANNEL_POLLING,
                "轮询结果通知",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "每次轮询成功或失败时发送通知"
            }

            manager.createNotificationChannel(foreground)
            manager.createNotificationChannel(polling)
        }
    }

    /** 发送轮询通知 */
    fun sendPollingNotification(context: Context, success: Boolean) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val statusText = if (success) "请求成功" else "请求失败"

        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            .format(Date())

        val content = "$statusText · $timestamp"

        val notification = NotificationCompat.Builder(context, CHANNEL_POLLING)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Notifier 轮询结果")
            .setContentText(content)
            .setAutoCancel(true)
            .build()

        // 使用随机 ID，避免覆盖旧通知
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
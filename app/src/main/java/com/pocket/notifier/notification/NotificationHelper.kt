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
import com.pocket.notifier.store.StoredMessage
import java.util.*

object NotificationHelper {

    /** ForegroundService 常驻通知渠道 */
    const val CHANNEL_FOREGROUND = "notifier_3_foreground"

    const val CHANNEL_NEW_MESSAGE = "notifier_1_new_message"
    const val CHANNEL_ERROR = "notifier_2_error"

    fun initChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)

            val msg = NotificationChannel(
                CHANNEL_NEW_MESSAGE,
                "New Message Notification",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new messages."
            }

            val err = NotificationChannel(
                CHANNEL_ERROR,
                "Error Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Network or request errors."
            }

            val fg = NotificationChannel(
                CHANNEL_FOREGROUND,
                "Background Service Notification",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notify the user that the notifier is running."
            }

            manager.createNotificationChannel(msg)
            manager.createNotificationChannel(err)
            manager.createNotificationChannel(fg)
        }
    }

    /** Error notification */
    fun sendError(context: Context, message: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, CHANNEL_ERROR)
            .setSmallIcon(R.drawable.ic_notify)
            .setContentTitle("Error")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    /** ------------------------------
    * 1) 最底层：发送通知（只负责 title + content）
    * ------------------------------ */
    private fun notifyMessage(context: Context, title: String, content: String) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, CHANNEL_NEW_MESSAGE)
            .setSmallIcon(R.drawable.ic_notify)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setAutoCancel(true)
            .build()

        // 使用随机 ID，避免覆盖旧通知
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }


    /** ------------------------------
    * 2) 处理单条消息：返回 Pair<title, content>
    * ------------------------------ */
    private fun buildSingleMessageNotification(message: StoredMessage): Pair<String, String> {
        // 作者显示优先使用 name，没有则使用 username
        val name = if (message.authorName.isNotEmpty()) {
            message.authorName
        } else {
            message.authorUsername
        }

        val title = name
        val content = message.content // 你原本的逻辑：不处理空内容

        return title to content
    }


    /** ------------------------------
    * 3) 处理多条消息：返回 Pair<title, content>
    * ------------------------------ */
    private fun buildMultiMessageNotification(messages: List<StoredMessage>, perPage: Int): Pair<String, String> {

        // ---------- 标题 ----------
        val title = if (messages.size >= perPage) {
            "${perPage}+ New Messages"
        } else {
            "${messages.size} New Messages"
        }

        // ---------- 内容：列出用户 ----------
        val users = messages.map {
            if (it.authorName.isNotEmpty()) it.authorName else it.authorUsername
        }.distinct()

        val content = when {
            users.size <= 4 -> "from ${users.joinToString(", ")}."
            else -> "from ${users.size} people: ${users.take(3).joinToString(", ")}, and others."
        }

        return title to content
    }

    /** ------------------------------
    * 用于多条消息（入口函数）
    * 根据消息数量自动选择单条/多条逻辑
    * ------------------------------ */
    fun sendNewMessages(context: Context, messages: List<StoredMessage>, perPage: Int) {
        if (messages.isEmpty()) return

        val (title, content) = if (messages.size == 1) {
            // 只有一条 → 使用单条构建逻辑
            buildSingleMessageNotification(messages.first())
        } else {
            // 多条 → 使用多条构建逻辑
            buildMultiMessageNotification(messages, perPage)
        }

        notifyMessage(context, title, content)
    }

    /** ------------------------------
    * 用于单条消息（入口函数）
    * 外部如果明确知道只有一条消息，可以直接调用这个
    * ------------------------------ */
    fun sendNewMessage(context: Context, message: StoredMessage) {
        val (title, content) = buildSingleMessageNotification(message)
        notifyMessage(context, title, content)
    }

}
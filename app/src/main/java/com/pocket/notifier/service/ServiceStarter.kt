package com.pocket.notifier.service

/**
 * ServiceStarter — 用于启动 ForegroundService
 *
 * 你可以在 MainActivity 或 Application 中调用：
 * ServiceStarter.start(context)
 */

import android.content.Context
import android.content.Intent
import android.os.Build

object ServiceStarter {

    fun start(context: Context) {
        val intent = Intent(context, PollingService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }
}
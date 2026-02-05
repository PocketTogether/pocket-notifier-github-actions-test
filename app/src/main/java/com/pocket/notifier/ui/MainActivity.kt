package com.pocket.notifier.ui

/**
 * MainActivity — 单页面 UI
 *
 * 仅负责：
 * - 读取上次请求状态（由 StatusStore 提供）
 * - 根据状态显示 success / failure 图片
 *
 * 不包含任何业务逻辑（轮询、网络、通知等）
 */

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pocket.notifier.databinding.ActivityMainBinding
import com.pocket.notifier.store.StatusStore
import com.pocket.notifier.R
import com.pocket.notifier.service.ServiceStarter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ⭐ 启动后台轮询服务 
        ServiceStarter.start(this)

        updateImage()
    }

    override fun onResume() {
        super.onResume()

        // 📌 当 Activity 进入前台（用户可见）时注册广播接收器
        // 这样只有在用户真正看到页面时才会接收 Service 发来的更新事件
        registerReceiver(statusReceiver, IntentFilter("NOTIFIER_STATUS_UPDATED"))

        // 📌 刚进入前台时立即刷新一次 UI（避免显示旧状态）
        updateImage()
    }

    override fun onPause() {
        super.onPause()

        // 📌 当 Activity 不再可见时取消注册广播
        // 这是 Android 的最佳实践：避免内存泄漏、避免后台无意义刷新
        unregisterReceiver(statusReceiver)
    }

    // 📌 广播接收器：当 Service 发送 “NOTIFIER_STATUS_UPDATED” 广播时触发
    // 每次触发都会调用 updateImage()，实现真正的“实时刷新”
    private val statusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            // 📌 收到 Service 的状态更新广播 → 立即刷新 UI
            updateImage()
        }
    }

    /** 根据上次请求状态切换图片 */
    private fun updateImage() {
        val lastSuccess = StatusStore.getLastStatus(this)

        val imageRes = if (lastSuccess) {
            R.drawable.success
        } else {
            R.drawable.failure
        }

        binding.statusImage.setImageResource(imageRes)
    }
}
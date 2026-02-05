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
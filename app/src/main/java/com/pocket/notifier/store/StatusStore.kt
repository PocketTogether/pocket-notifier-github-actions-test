package com.pocket.notifier.store

/**
 * StatusStore — 轮询状态存储
 *
 * 功能：
 * - 记录上次请求是否成功（true/false）
 * - 提供 getLastStatus() / setLastStatus()
 * - 使用 SharedPreferences（轻量、稳定、可持久化）
 *
 * 默认值：false（表示失败）
 */

import android.content.Context

object StatusStore {

    private const val PREF_NAME = "notifier_status"
    private const val KEY_LAST_SUCCESS = "last_success"

    /** 获取上次请求是否成功 */
    fun getLastStatus(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_LAST_SUCCESS, false)
    }

    /** 设置上次请求是否成功 */
    fun setLastStatus(context: Context, success: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(KEY_LAST_SUCCESS, success)
            .apply()
    }
}
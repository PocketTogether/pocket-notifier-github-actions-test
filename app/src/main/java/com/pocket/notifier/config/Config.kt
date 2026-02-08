/**
 * ================================
 * 🚩 常改位置（集中配置）
 * ================================
 *
 * 你可以在这里修改：
 * - 轮询间隔（秒）
 * - 请求超时时间（秒）
 * - 请求路径（API URL）
 * - 实时 SSE 相关配置
 *
 * 所有配置均为 const，编译期常量，便于统一管理。
 */

package com.pocket.notifier.config

object Config {
    /**
     * 网站url，应修改
     */
    const val POCKETCHAT_BASE_URL: String = "https://uika.top"

    /** 点击主图片时打开的网址 */
    const val CLICK_URL: String = "${POCKETCHAT_BASE_URL}"

    /** 轮询请求路径 */
    const val REQUEST_URL: String =
        "${POCKETCHAT_BASE_URL}/api/collections/messages/records?page=1&perPage=40&expand=author&sort=-created%2Cid&skipTotal=true"

    /** SSE 实时连接地址（GET /api/realtime & POST /api/realtime） */
    const val REALTIME_URL: String = "${POCKETCHAT_BASE_URL}/api/realtime"

    /** 请求超时时间（秒） */
    const val REQUEST_TIMEOUT_SECONDS: Long = 10

    /** 轮询间隔（秒） */
    const val POLLING_INTERVAL_SECONDS: Long = 150

    /**
     * 单次 SSE 会话时长（秒）
     *
     * 浏览器端是约 1 分钟断开重连，好像是cf导致的，这里再稍短一些更可控
     */
    const val REALTIME_SESSION_SECONDS: Long = 55

    /**
     * SSE 订阅字符串
     * options={"query":{"expand":"author"}}
     */
    const val REALTIME_SUBSCRIPTION: String =
        "messages/*?options=%7B%22query%22%3A%7B%22expand%22%3A%22author%22%7D%7D"

}
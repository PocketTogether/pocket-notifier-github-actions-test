package com.pocket.notifier.store

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import com.pocket.notifier.config.Config

/**
 * MessageStore — stores all received messages in ascending order.
 *
 * Stored format:
 * [
 *   { id: "...", created: "...", content: "...", authorName: "...", authorUsername: "..." },
 *   ...
 * ]
 */
object MessageStore {

    private const val PREF_NAME = "notifier_messages"
    private const val KEY_MESSAGES = "messages"

    /** Load stored messages (ascending order) */
    fun getMessages(context: Context): MutableList<StoredMessage> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_MESSAGES, "[]") ?: "[]"

        val arr = JSONArray(json)
        val list = mutableListOf<StoredMessage>()

        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            list.add(
                StoredMessage(
                    id = obj.getString("id"),
                    created = obj.getString("created"),
                    content = obj.getString("content"),
                    authorName = obj.getString("authorName"),
                    authorUsername = obj.getString("authorUsername")
                )
            )
        }
        return list
    }

    /** Append new messages (ascending order) */
    fun addMessages(context: Context, newMessages: List<StoredMessage>) {
        if (newMessages.isEmpty()) return

        val list = getMessages(context)
        list.addAll(newMessages)

        // ⭐ 如果超过上限，裁剪为只保留最新 N 条
        if (list.size > Config.MESSAGE_STORE_MAX) {
            val start = list.size - Config.MESSAGE_STORE_TRIM_TO
            val trimmed = list.subList(start, list.size).toMutableList()
            saveList(context, trimmed)
            return
        }

        saveList(context, list)
    }

    private fun saveList(context: Context, list: List<StoredMessage>) {
        val arr = JSONArray()
        list.forEach {
            val obj = JSONObject()
            obj.put("id", it.id)
            obj.put("created", it.created)
            obj.put("content", it.content)
            obj.put("authorName", it.authorName)
            obj.put("authorUsername", it.authorUsername)
            arr.put(obj)
        }

        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_MESSAGES, arr.toString()).apply()
    }

    /** Get last message ID (or null) */
    fun getLastMessageId(context: Context): String? {
        val list = getMessages(context)
        return list.lastOrNull()?.id
    }
}

data class StoredMessage(
    val id: String,
    val created: String,
    val content: String,
    val authorName: String,
    val authorUsername: String
)
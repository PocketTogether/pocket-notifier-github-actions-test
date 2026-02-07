package com.pocket.notifier.store

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

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
package com.lecteur.iptv.data

import android.content.Context

class Prefs(context: Context) {
    private val sp = context.getSharedPreferences("iptv", Context.MODE_PRIVATE)

    fun load(): ServerConfig? {
        val url = sp.getString("url", null) ?: return null
        return ServerConfig(url, sp.getString("user", "") ?: "", sp.getString("pass", "") ?: "")
    }

    fun save(c: ServerConfig) {
        sp.edit().putString("url", c.url).putString("user", c.username).putString("pass", c.password).apply()
    }

    fun clear() { sp.edit().clear().apply() }

    var lastChannelUrl: String?
        get() = sp.getString("last", null)
        set(v) { sp.edit().putString("last", v).apply() }
}

package com.lecteur.iptv.data

import android.net.Uri

data class Channel(
    val id: Int,
    val name: String,
    val url: String,
    val logo: String?,
    val group: String,
)

/** Adresse du serveur. Soit une URL M3U complète, soit l'adresse Xtream + identifiants. */
data class ServerConfig(
    val url: String,
    val username: String = "",
    val password: String = "",
) {
    fun playlistUrl(): String {
        var base = url.trim()
        if (!base.startsWith("http://") && !base.startsWith("https://")) base = "http://$base"
        if (username.isBlank()) return base
        val root = base.substringBefore("/get.php").trimEnd('/')
        return "$root/get.php?username=${Uri.encode(username.trim())}" +
            "&password=${Uri.encode(password.trim())}&type=m3u_plus&output=ts"
    }
}

package com.lecteur.iptv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.lecteur.iptv.MainViewModel

@Composable
fun IptvApp(vm: MainViewModel) {
    val ui = vm.ui
    var editing by remember { mutableStateOf(false) }
    val playing = ui.playing

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        when {
            playing != null -> PlayerScreen(
                channel = playing,
                onNext = { vm.step(+1) },
                onPrev = { vm.step(-1) },
                onBack = vm::stop,
            )
            ui.config == null || editing -> SetupScreen(
                initial = ui.config,
                error = ui.error,
                onSave = { editing = false; vm.saveConfig(it) },
                onCancel = if (ui.config != null) ({ editing = false }) else null,
                onLogout = if (ui.config != null) ({ editing = false; vm.logout() }) else null,
            )
            else -> HomeScreen(vm, onSettings = { editing = true })
        }
    }
}

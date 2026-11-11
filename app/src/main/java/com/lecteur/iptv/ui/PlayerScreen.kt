package com.lecteur.iptv.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.lecteur.iptv.data.Channel
import com.lecteur.iptv.data.USER_AGENT
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(channel: Channel, onNext: () -> Unit, onPrev: () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val player = remember {
        val http = DefaultHttpDataSource.Factory()
            .setUserAgent(USER_AGENT)
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(15_000)
            .setReadTimeoutMs(20_000)
        ExoPlayer.Builder(context).setMediaSourceFactory(DefaultMediaSourceFactory(http)).build()
    }
    var error by remember { mutableStateOf<String?>(null) }
    var buffering by remember { mutableStateOf(true) }
    var overlay by remember { mutableStateOf(true) }
    var tick by remember { mutableIntStateOf(0) }
    val focus = remember { FocusRequester() }

    DisposableEffect(player) {
        val l = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) { buffering = state == Player.STATE_BUFFERING }
            override fun onPlayerError(e: PlaybackException) { error = "Lecture impossible (${e.errorCodeName})" }
        }
        player.addListener(l)
        onDispose { player.removeListener(l); player.release() }
    }

    LaunchedEffect(channel.url) {
        error = null; buffering = true; tick++
        player.setMediaItem(MediaItem.fromUri(channel.url))
        player.prepare()
        player.playWhenReady = true
    }
    LaunchedEffect(tick) { overlay = true; delay(4000); overlay = false }
    LaunchedEffect(Unit) { focus.requestFocus() }
    BackHandler(onBack = onBack)

    Box(
        Modifier.fillMaxSize().background(Color.Black)
            .focusRequester(focus).focusable()
            .onKeyEvent { e ->
                if (e.type != KeyEventType.KeyDown) return@onKeyEvent false
                when (e.key) {
                    Key.DirectionUp, Key.ChannelUp, Key.PageUp -> { onNext(); true }
                    Key.DirectionDown, Key.ChannelDown, Key.PageDown -> { onPrev(); true }
                    Key.DirectionCenter, Key.Enter, Key.NumPadEnter -> { tick++; true }
                    else -> false
                }
            }
            .pointerInput(Unit) { detectTapGestures { tick++ } },
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PlayerView(ctx).apply {
                    useController = false
                    isFocusable = false
                    keepScreenOn = true
                }
            },
            update = { it.player = player },
        )
        if (buffering && error == null) CircularProgressIndicator(Modifier.align(Alignment.Center))
        error?.let { msg ->
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(msg, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
                Text("Changez de chaîne avec ▲ ▼", color = Color.White)
            }
        }
        if (overlay) {
            Row(
                Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                    .background(Color(0xCC000000)).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(channel.name, color = Color.White, style = MaterialTheme.typography.titleLarge)
                    Text(channel.group, color = Color.LightGray, style = MaterialTheme.typography.bodyMedium)
                }
                IconButton(onClick = onPrev) { Icon(Icons.Default.KeyboardArrowDown, "Précédente", tint = Color.White) }
                IconButton(onClick = onNext) { Icon(Icons.Default.KeyboardArrowUp, "Suivante", tint = Color.White) }
            }
        }
    }
}

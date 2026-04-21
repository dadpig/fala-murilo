package com.tairone.falamurilo.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tairone.falamurilo.engine.AudioRecorder
import com.tairone.falamurilo.models.CardItem
import com.tairone.falamurilo.store.CardStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceRecorderSheet(
    card: CardItem,
    store: CardStore,
    onDismiss: () -> Unit,
) {
    val recorder = remember { AudioRecorder() }
    val isRecording by recorder.isRecording.collectAsState()
    val hasRecording by recorder.hasRecording.collectAsState()
    val duration by recorder.durationSecs.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    val label = store.displayLabel(card.id, card.label(store.language))
    val outputFile = store.audioFile(card.id)

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) recorder.start(outputFile) }

    DisposableEffect(Unit) {
        onDispose { if (isRecording) recorder.stop() }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFFDF8F0),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            MiniPolaroid(card = card, store = store, size = 130.dp)

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                when {
                    isRecording -> {
                        Text("Gravando…", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Red)
                        Text("${duration}s", fontSize = 14.sp, color = Color.Gray)
                    }
                    hasRecording || store.hasAudio(card.id) -> {
                        Text("Voz gravada ✓", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF4CAF50))
                        Text("Toque ▶ para ouvir", fontSize = 14.sp, color = Color.Gray)
                    }
                    else -> {
                        Text("Toque para gravar", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            "Sua voz vai tocar ao usar este cartão",
                            fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (isRecording) PulsingRings()

            Row(
                horizontalArrangement = Arrangement.spacedBy(28.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (hasRecording || store.hasAudio(card.id)) {
                    IconButton(onClick = { recorder.playPreview(outputFile) }) {
                        Icon(
                            Icons.Default.PlayArrow, contentDescription = "Ouvir",
                            modifier = Modifier.size(52.dp), tint = Color(0xFF6C63FF)
                        )
                    }
                } else {
                    Spacer(Modifier.size(52.dp))
                }

                IconButton(
                    onClick = {
                        if (isRecording) recorder.stop()
                        else permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    },
                    modifier = Modifier
                        .size(76.dp)
                        .background(
                            if (isRecording) Color.Red else Color(0xFFFF6B6B),
                            CircleShape
                        )
                ) {
                    Icon(
                        if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (isRecording) "Parar" else "Gravar",
                        modifier = Modifier.size(34.dp), tint = Color.White
                    )
                }

                if (store.hasAudio(card.id)) {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete, contentDescription = "Apagar",
                            modifier = Modifier.size(52.dp), tint = Color.Red.copy(alpha = 0.65f)
                        )
                    }
                } else {
                    Spacer(Modifier.size(52.dp))
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Apagar voz?") },
            text = { Text("Apagar a voz gravada para \"$label\"?") },
            confirmButton = {
                TextButton(onClick = {
                    store.clearAudio(card.id)
                    recorder.reset()
                    showDeleteDialog = false
                }) { Text("Apagar", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun PulsingRings() {
    val inf = rememberInfiniteTransition(label = "pulse")
    Box(Modifier.size(80.dp), contentAlignment = Alignment.Center) {
        repeat(3) { i ->
            val scale by inf.animateFloat(
                initialValue = 1f, targetValue = 1.8f + i * 0.3f,
                animationSpec = infiniteRepeatable(
                    tween(1200, delayMillis = i * 300, easing = LinearEasing),
                    RepeatMode.Restart
                ), label = "ring$i"
            )
            val alpha by inf.animateFloat(
                initialValue = 0.6f, targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    tween(1200, delayMillis = i * 300, easing = LinearEasing),
                    RepeatMode.Restart
                ), label = "alpha$i"
            )
            Box(
                Modifier
                    .size(24.dp)
                    .scale(scale)
                    .background(Color.Red.copy(alpha = alpha), CircleShape)
            )
        }
        Box(Modifier.size(14.dp).background(Color.Red, CircleShape))
    }
}

@Composable
fun MiniPolaroid(card: CardItem, store: CardStore, size: androidx.compose.ui.unit.Dp = 72.dp) {
    val label = store.displayLabel(card.id, card.label(store.language))
    val photo = remember(store.version) { store.loadPhoto(card.id) }

    Box(
        Modifier
            .graphicsLayer { rotationZ = -1.5f }
            .shadow(8.dp, RoundedCornerShape(2.dp))
            .clip(RoundedCornerShape(2.dp))
            .background(Color.White)
    ) {
        Column {
            Box(
                Modifier
                    .size(size)
                    .background(card.color),
                contentAlignment = Alignment.Center
            ) {
                if (photo != null) {
                    Image(
                        bitmap = photo.asImageBitmap(),
                        contentDescription = label,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(card.imageRes),
                        contentDescription = label,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Text(
                text = label,
                fontSize = (size.value * 0.09f).sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .width(size)
                    .background(Color.White)
                    .padding(horizontal = 3.dp, vertical = 6.dp),
                textAlign = TextAlign.Center,
                maxLines = 1,
                color = Color(0xFF1A1A1A)
            )
        }
    }
}

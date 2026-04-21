package com.tairone.falamurilo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tairone.falamurilo.engine.SpeechEngine
import com.tairone.falamurilo.models.CardItem
import com.tairone.falamurilo.store.CardStore

@Composable
fun ContentScreen(store: CardStore = viewModel()) {
    val context = LocalContext.current
    val speech = remember { SpeechEngine(context) }
    var showPreferences by remember { mutableStateOf(false) }

    DisposableEffect(Unit) { onDispose { speech.release() } }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF8F0))
            .systemBarsPadding()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize().padding(bottom = 80.dp)
        ) {
            itemsIndexed(CardItem.all, key = { _, c -> c.id }) { index, card ->
                CardView(
                    card = card,
                    index = index,
                    store = store,
                    onClick = {
                        if (store.hasAudio(card.id))
                            speech.playAudio(store.audioFile(card.id))
                        else
                            speech.speak(store.displayLabel(card.id, card.label(store.language)), store.language)
                    }
                )
            }
        }

        FloatingActionButton(
            onClick = { showPreferences = true },
            containerColor = Color.White.copy(alpha = 0.9f),
            contentColor = Color.Gray,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Preferências")
        }
    }

    if (showPreferences) {
        PreferencesSheet(store = store, onDismiss = { showPreferences = false })
    }
}

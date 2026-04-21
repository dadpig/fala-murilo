package com.tairone.falamurilo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tairone.falamurilo.models.AppLanguage
import com.tairone.falamurilo.models.CardItem
import com.tairone.falamurilo.store.CardStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesSheet(
    store: CardStore,
    onDismiss: () -> Unit,
) {
    var recorderCard by remember { mutableStateOf<CardItem?>(null) }
    var showAbout by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 48.dp)
        ) {
            item {
                SectionHeader("Idioma / Language")
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppLanguage.entries.forEach { lang ->
                        val selected = store.language == lang
                        FilterChip(
                            selected = selected,
                            onClick = { store.setLanguage(lang) },
                            label = {
                                Text(
                                    "${lang.flag} ${lang.label}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF6C63FF).copy(0.12f),
                                selectedLabelColor = Color(0xFF6C63FF),
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                selectedBorderColor = Color(0xFF6C63FF),
                                selectedBorderWidth = 1.5.dp,
                            )
                        )
                    }
                }
                HorizontalDivider(Modifier.padding(vertical = 4.dp))
            }

            item { SectionHeader("Cartões / Cards") }

            items(CardItem.all) { card ->
                Column {
                    CardEditorRow(
                        card = card,
                        store = store,
                        onRecordVoice = { recorderCard = card }
                    )
                    HorizontalDivider(
                        Modifier.padding(start = 16.dp),
                        color = Color(0xFFF0F0F0)
                    )
                }
            }

            item {
                HorizontalDivider(Modifier.padding(vertical = 4.dp))
                Surface(
                    onClick = { showAbout = true },
                    color = Color.Transparent,
                ) {
                    ListItem(
                        headlineContent = { Text("Sobre", fontWeight = FontWeight.SemiBold) },
                        leadingContent = {
                            Icon(
                                Icons.Default.Favorite, contentDescription = null,
                                tint = Color(0xFFFF6B6B)
                            )
                        },
                        trailingContent = {
                            Icon(Icons.Default.NavigateNext, contentDescription = null, tint = Color.LightGray)
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
    }

    recorderCard?.let { card ->
        VoiceRecorderSheet(
            card = card,
            store = store,
            onDismiss = { recorderCard = null }
        )
    }

    if (showAbout) {
        AboutSheet(onDismiss = { showAbout = false })
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        letterSpacing = 0.8.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 8.dp)
    )
}

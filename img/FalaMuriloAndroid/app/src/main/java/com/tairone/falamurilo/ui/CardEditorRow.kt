package com.tairone.falamurilo.ui

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tairone.falamurilo.models.CardItem
import com.tairone.falamurilo.store.CardStore

@Composable
fun CardEditorRow(
    card: CardItem,
    store: CardStore,
    onRecordVoice: () -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val defaultLabel = card.label(store.language)
    var labelText by remember(store.language, store.version) {
        mutableStateOf(store.customLabel(card.id) ?: defaultLabel)
    }

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val bmp: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
        store.savePhoto(card.id, bmp)
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MiniPolaroid(card = card, store = store, size = 62.dp)

            Column(Modifier.weight(1f)) {
                Text("Label", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                OutlinedTextField(
                    value = labelText,
                    onValueChange = { labelText = it },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        commitLabel(store, card.id, labelText, defaultLabel)
                        focusManager.clearFocus()
                    }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { fs ->
                            if (!fs.isFocused) commitLabel(store, card.id, labelText, defaultLabel)
                        },
                    shape = RoundedCornerShape(10.dp),
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionChip(
                onClick = {
                    photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                icon = Icons.Default.Photo,
                label = "Foto",
                tint = Color(0xFF6C63FF),
                bg = Color(0xFF6C63FF).copy(alpha = 0.1f)
            )

            if (store.hasPhoto(card.id)) {
                ActionChip(
                    onClick = { store.clearPhoto(card.id) },
                    icon = Icons.Default.Close,
                    label = "Remover",
                    tint = Color.Red,
                    bg = Color.Red.copy(alpha = 0.08f)
                )
            }

            Spacer(Modifier.weight(1f))

            ActionChip(
                onClick = onRecordVoice,
                icon = Icons.Default.Mic,
                label = if (store.hasAudio(card.id)) "Voz ✓" else "Gravar",
                tint = if (store.hasAudio(card.id)) Color(0xFF4CAF50) else Color(0xFFFF9800),
                bg = (if (store.hasAudio(card.id)) Color(0xFF4CAF50) else Color(0xFFFF9800)).copy(0.1f)
            )
        }
    }
}

private fun commitLabel(store: CardStore, cardId: String, text: String, default: String) {
    val trimmed = text.trim()
    store.setLabel(cardId, if (trimmed == default) "" else trimmed)
}

@Composable
private fun ActionChip(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    bg: Color,
) {
    Surface(onClick = onClick, color = bg, shape = RoundedCornerShape(8.dp)) {
        Row(
            Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(14.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = tint)
        }
    }
}

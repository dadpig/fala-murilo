package com.tairone.falamurilo.ui

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tairone.falamurilo.models.CardItem
import com.tairone.falamurilo.store.CardStore

@Composable
fun CardView(
    card: CardItem,
    index: Int,
    store: CardStore,
    onClick: () -> Unit,
) {
    var pressed by remember { mutableStateOf(false) }
    val tilt = if (index % 2 == 0) -1.4f else 1.4f

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.91f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 300f),
        label = "scale"
    )
    val rotation by animateFloatAsState(
        targetValue = if (pressed) 0f else tilt,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 300f),
        label = "rotation"
    )

    val label = store.displayLabel(card.id, card.label(store.language))
    val customPhoto: Bitmap? = remember(store.version) { store.loadPhoto(card.id) }

    Box(
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale; rotationZ = rotation }
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(3.dp))
            .clip(RoundedCornerShape(3.dp))
            .background(Color.White)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                    },
                    onTap = { onClick() }
                )
            }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .background(card.color)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (customPhoto != null) {
                    Image(
                        bitmap = customPhoto.asImageBitmap(),
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 4.dp)
                    .padding(top = 7.dp, bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF1A1A1A)
                    ),
                    maxLines = 2,
                )
                if (store.hasAudio(card.id)) {
                    Spacer(Modifier.height(3.dp))
                    Text("〰", fontSize = 9.sp, color = Color(0xFF6C63FF))
                }
            }
        }
    }
}

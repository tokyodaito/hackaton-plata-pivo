package diftech.hackathon.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

private data class BeerBubble(
    val xPosition: Float,
    val offset: Float,
    val size: Dp,
    val alpha: Float
)

@Composable
fun BeerOverlay(onDismiss: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2600)
        onDismiss()
    }

    val transition = rememberInfiniteTransition(label = "beer-overlay")
    val foamPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(
                durationMillis = 2600,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "foam-phase"
    )
    val bubbleProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(
                durationMillis = 4200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "bubble-progress"
    )

    val bubbles = remember {
        List(26) {
            BeerBubble(
                xPosition = Random.nextFloat(),
                offset = Random.nextFloat(),
                size = Random.nextInt(10, 32).dp,
                alpha = Random.nextFloat() * 0.35f + 0.15f
            )
        }
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF4C2),
                        Color(0xFFF5B739),
                        Color(0xFFB56D07)
                    )
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onDismiss
            )
    ) {
        BeerBubblesLayer(bubbles = bubbles, progress = bubbleProgress)
        BeerFoam(foamPhase = foamPhase)
        BeerMessage()
    }
}

@Composable
private fun BeerBubblesLayer(bubbles: List<BeerBubble>, progress: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val bubbleColor = Color.White
        bubbles.forEach { bubble ->
            val currentProgress = (progress + bubble.offset) % 1f
            val y = size.height * (1f - currentProgress)
            if (y < size.height * 0.12f) return@forEach

            val radius = bubble.size.toPx() / 2f
            val centerX = bubble.xPosition * size.width
            drawCircle(
                color = bubbleColor.copy(alpha = bubble.alpha),
                radius = radius,
                center = Offset(centerX, y)
            )
            drawCircle(
                color = bubbleColor.copy(alpha = bubble.alpha * 0.6f),
                radius = radius * 0.5f,
                center = Offset(centerX - radius * 0.35f, y - radius * 0.45f)
            )
        }
    }
}

@Composable
private fun BeerFoam(foamPhase: Float) {
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .align(Alignment.TopCenter)
        ) {
            val foamColor = Color(0xFFFFFAEB)
            val highlightColor = Color.White.copy(alpha = 0.85f)
            val path = Path().apply {
                moveTo(0f, 0f)
                val waveCount = 6
                val step = size.width / waveCount
                val amplitude = size.height * 0.3f
                for (i in 0..waveCount) {
                    val x = i * step
                    val y = amplitude + sin(foamPhase + i * 0.9f) * amplitude * 0.35f
                    lineTo(x, y)
                }
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            drawPath(path = path, color = foamColor)
            drawPath(
                path = path,
                color = highlightColor,
                style = Stroke(width = size.height * 0.06f)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.7f), Color.Transparent)
                    )
                )
        )
    }
}

@Composable
private fun BeerMessage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 48.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Surface(
            color = Color.White.copy(alpha = 0.15f),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "🍺 Будьмо!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ты нашёл пасхалку — экран наполнился пивом, когда ты потряс приложение!",
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.92f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(0.95f)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1.2f))
    }
}

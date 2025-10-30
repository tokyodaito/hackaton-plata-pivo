package diftech.hackathon.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.isActive
import androidx.compose.runtime.withFrameNanos
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private val BackgroundGradient = listOf(
    Color(0xFF0A0E2A),
    Color(0xFF1F1B4D),
    Color(0xFF090F2D)
)

private val AccentGradient = listOf(
    Color(0xFF6DD5FA),
    Color(0xFFB983FF),
    Color(0xFFFF7EE5)
)

@Composable
fun LiquidGlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.linearGradient(BackgroundGradient))
            .drawWithContent {
                drawContent()
                val glowColor = AccentGradient.random()
                drawCircle(
                    color = glowColor.copy(alpha = 0.18f),
                    radius = size.minDimension * 0.6f,
                    center = Offset(size.width * 0.2f, size.height * 0.15f)
                )
                drawCircle(
                    color = glowColor.copy(alpha = 0.12f),
                    radius = size.minDimension * 0.5f,
                    center = Offset(size.width * 0.8f, size.height * 0.85f)
                )
            }
    ) {
        content()
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 26.dp,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Column(
        modifier = modifier
            .clip(shape)
            .background(Color.White.copy(alpha = 0.08f))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.45f),
                        Color.White.copy(alpha = 0.1f)
                    )
                ),
                shape = shape
            )
            .drawWithContent {
                drawContent()
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.22f),
                            Color.Transparent
                        ),
                        start = Offset.Zero,
                        end = Offset(size.width, size.height)
                    ),
                    alpha = 0.4f
                )
            }
            .padding(contentPadding),
        content = content
    )
}

@Composable
fun GlassButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)
    val gradient = Brush.linearGradient(AccentGradient)
    Box(
        modifier = modifier
            .clip(shape)
            .background(Color.White.copy(alpha = 0.12f))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.7f),
                        Color.White.copy(alpha = 0.2f)
                    )
                ),
                shape = shape
            )
            .alpha(if (enabled) 1f else 0.6f)
            .clickable(enabled = enabled && !loading, onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            }
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                letterSpacing = 0.5.sp,
                modifier = Modifier.drawWithContent {
                    drawContent()
                    drawRect(gradient, alpha = 0.35f)
                }
            )
        }
    }
}

@Composable
fun LiquidLoader(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "loader")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "loaderRotation"
    )

    Canvas(modifier = modifier) {
        val strokeWidth = size.minDimension * 0.12f
        rotate(rotation) {
            drawArc(
                brush = Brush.sweepGradient(AccentGradient),
                startAngle = 20f,
                sweepAngle = 300f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
    }
}

private data class Particle(
    val position: Offset,
    val velocity: Offset,
    val radius: Float,
    val life: Float,
    val color: Color
) {
    fun advance(delta: Float): Particle = copy(
        position = position + velocity * delta,
        life = life - delta
    )
}

@Composable
fun DoDepParticles(
    modifier: Modifier = Modifier,
    isActive: Boolean
) {
    var particles by remember { mutableStateOf(listOf<Particle>()) }
    val random = remember { Random(System.currentTimeMillis()) }

    LaunchedEffect(isActive) {
        if (!isActive) {
            particles = emptyList()
            return@LaunchedEffect
        }
        var lastTime = 0L
        while (isActive && this.isActive) {
            val frameTime = withFrameNanos { it }
            if (lastTime == 0L) {
                lastTime = frameTime
                continue
            }
            val delta = ((frameTime - lastTime).coerceAtMost(50_000_000L)).toFloat() / 1_000_000_000f
            lastTime = frameTime

            val updated = particles
                .map { it.advance(delta) }
                .filter { it.life > 0f && it.position.x in -0.2f..1.2f && it.position.y in -0.2f..1.2f }

            val nextParticles = updated.toMutableList()
            repeat(4) {
                if (nextParticles.size > 120) return@repeat
                val angle = random.nextFloat() * 360f
                val speed = 0.25f + random.nextFloat() * 0.35f
                val angleRad = angle * (PI.toFloat() / 180f)
                val velocity = Offset(
                    x = cos(angleRad.toDouble()).toFloat() * speed,
                    y = sin(angleRad.toDouble()).toFloat() * speed
                )
                nextParticles += Particle(
                    position = Offset(
                        x = 0.5f + (random.nextFloat() - 0.5f) * 0.2f,
                        y = 0.5f + (random.nextFloat() - 0.5f) * 0.2f
                    ),
                    velocity = velocity,
                    radius = random.nextFloat() * 8f + 4f,
                    life = 1.4f + random.nextFloat() * 1.2f,
                    color = AccentGradient[random.nextInt(AccentGradient.size)].copy(alpha = 0.8f)
                )
            }
            particles = nextParticles
        }
    }

    Canvas(modifier = modifier) {
        particles.forEach { particle ->
            val center = Offset(
                x = particle.position.x * size.width,
                y = particle.position.y * size.height
            )
            val alpha = (particle.life / 2f).coerceIn(0f, 1f)
            drawCircle(
                color = particle.color.copy(alpha = alpha),
                radius = particle.radius,
                center = center
            )
        }
    }
}

@Composable
fun DoDepLoadingOverlay(
    modifier: Modifier = Modifier,
    title: String = "До-деп выполняется..."
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF05070F).copy(alpha = 0.65f)),
        contentAlignment = Alignment.Center
    ) {
        DoDepParticles(
            modifier = Modifier.fillMaxSize(),
            isActive = true
        )

        GlassCard(
            modifier = Modifier
                .padding(horizontal = 32.dp),
            cornerRadius = 32.dp,
            contentPadding = PaddingValues(horizontal = 28.dp, vertical = 24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LiquidLoader(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally)
                        .size(96.dp)
                )
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private operator fun Offset.plus(other: Offset): Offset = Offset(x + other.x, y + other.y)
private operator fun Offset.times(factor: Float): Offset = Offset(x * factor, y * factor)

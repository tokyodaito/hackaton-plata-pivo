package diftech.hackathon.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import diftech.hackathon.ui.components.GlassCard
import kotlin.math.roundToInt

@Composable
fun CryptoChart(
    priceHistory: List<Double>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFF2196F3)
) {
    if (priceHistory.isEmpty() || priceHistory.size < 2) return

    val chartPadding = 40f
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var selectedPoint by remember { mutableStateOf<Offset?>(null) }
    var selectedPrice by remember { mutableStateOf<Double?>(null) }
    var chartSize by remember { mutableStateOf(androidx.compose.ui.unit.IntSize.Zero) }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { chartSize = it }
            .height(260.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(horizontal = 4.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .pointerInput(priceHistory, chartSize) {
                        if (chartSize.width == 0 || chartSize.height == 0) return@pointerInput
                        detectTapGestures { position ->
                            val width = chartSize.width.toFloat()
                            val height = chartSize.height.toFloat()
                            val maxPrice = priceHistory.maxOrNull() ?: return@detectTapGestures
                            val minPrice = priceHistory.minOrNull() ?: return@detectTapGestures
                            val priceRange = maxPrice - minPrice
                            if (priceRange == 0.0) return@detectTapGestures

                            val stepX = (width - 2 * chartPadding) / (priceHistory.size - 1)
                            val clampedX = position.x.coerceIn(chartPadding, width - chartPadding)
                            val index = ((clampedX - chartPadding) / stepX).roundToInt()
                                .coerceIn(0, priceHistory.lastIndex)

                            val price = priceHistory[index]
                            val normalized = ((price - minPrice) / priceRange).toFloat()
                            val y = height - chartPadding - (height - 2 * chartPadding) * normalized
                            val x = chartPadding + index * stepX
                            selectedIndex = index
                            selectedPoint = Offset(x, y)
                            selectedPrice = price
                        }
                    }
            ) {
                val width = size.width
                val height = size.height
                val maxPrice = priceHistory.maxOrNull() ?: return@Canvas
                val minPrice = priceHistory.minOrNull() ?: return@Canvas
                val priceRange = maxPrice - minPrice
                if (priceRange == 0.0) return@Canvas

                val stepX = (width - 2 * chartPadding) / (priceHistory.size - 1)

                val gridColor = Color.White.copy(alpha = 0.08f)
                for (i in 0..4) {
                    val y = chartPadding + (height - 2 * chartPadding) * i / 4
                    drawLine(
                        color = gridColor,
                        start = Offset(chartPadding, y),
                        end = Offset(width - chartPadding, y),
                        strokeWidth = 1.2f
                    )
                }

                val fillPath = Path().apply {
                    moveTo(chartPadding, height - chartPadding)
                    priceHistory.forEachIndexed { index, price ->
                        val x = chartPadding + index * stepX
                        val normalized = ((price - minPrice) / priceRange).toFloat()
                        val y = height - chartPadding - (height - 2 * chartPadding) * normalized
                        lineTo(x, y)
                    }
                    lineTo(width - chartPadding, height - chartPadding)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            lineColor.copy(alpha = 0.35f),
                            lineColor.copy(alpha = 0.05f)
                        )
                    ),
                    style = Fill
                )

                val path = Path().apply {
                    priceHistory.forEachIndexed { index, price ->
                        val x = chartPadding + index * stepX
                        val normalized = ((price - minPrice) / priceRange).toFloat()
                        val y = height - chartPadding - (height - 2 * chartPadding) * normalized
                        if (index == 0) {
                            moveTo(x, y)
                        } else {
                            lineTo(x, y)
                        }
                    }
                }

                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                selectedIndex?.let { index ->
                    selectedPoint?.let { point ->
                        drawLine(
                            color = lineColor.copy(alpha = 0.3f),
                            start = Offset(point.x, chartPadding),
                            end = Offset(point.x, height - chartPadding),
                            strokeWidth = 2f
                        )

                        drawCircle(
                            color = Color.White,
                            radius = 9f,
                            center = point
                        )
                        drawCircle(
                            color = lineColor,
                            radius = 6f,
                            center = point
                        )
                    }
                }
            }

            selectedPrice?.let { price ->
                val point = selectedPoint
                if (point != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset {
                                val x = (point.x - 70f).roundToInt()
                                val y = (point.y - 70f).roundToInt()
                                IntOffset(x, y)
                            }
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(
                                        Color(0xFF141832).copy(alpha = 0.9f),
                                        Color(0xFF2D2F4F).copy(alpha = 0.8f)
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "$${String.format("%.2f", price)}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

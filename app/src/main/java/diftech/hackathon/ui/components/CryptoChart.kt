package diftech.hackathon.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun CryptoChart(
    priceHistory: List<Double>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFF2196F3)
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        if (priceHistory.isEmpty() || priceHistory.size < 2) return@Canvas
        
        val width = size.width
        val height = size.height
        val padding = 40f
        
        val maxPrice = priceHistory.maxOrNull() ?: return@Canvas
        val minPrice = priceHistory.minOrNull() ?: return@Canvas
        val priceRange = maxPrice - minPrice
        
        if (priceRange == 0.0) return@Canvas
        
        val stepX = (width - 2 * padding) / (priceHistory.size - 1)
        
        // Рисуем сетку
        val gridColor = Color.LightGray.copy(alpha = 0.3f)
        for (i in 0..4) {
            val y = padding + (height - 2 * padding) * i / 4
            drawLine(
                color = gridColor,
                start = Offset(padding, y),
                end = Offset(width - padding, y),
                strokeWidth = 1f
            )
        }
        
        // Рисуем линию графика
        val path = Path()
        priceHistory.forEachIndexed { index, price ->
            val x = padding + index * stepX
            val normalizedPrice = ((price - minPrice) / priceRange).toFloat()
            val y = height - padding - (height - 2 * padding) * normalizedPrice
            
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(
                width = 3f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
        
        // Рисуем точки на графике
        priceHistory.forEachIndexed { index, price ->
            val x = padding + index * stepX
            val normalizedPrice = ((price - minPrice) / priceRange).toFloat()
            val y = height - padding - (height - 2 * padding) * normalizedPrice
            
            drawCircle(
                color = lineColor,
                radius = 4f,
                center = Offset(x, y)
            )
        }
    }
}

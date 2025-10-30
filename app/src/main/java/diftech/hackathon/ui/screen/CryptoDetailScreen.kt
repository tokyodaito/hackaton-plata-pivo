package diftech.hackathon.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import diftech.hackathon.data.model.Crypto
import diftech.hackathon.data.repository.CryptoRepository
import diftech.hackathon.data.repository.MockCryptoRepository
import diftech.hackathon.ui.components.CryptoChart
import diftech.hackathon.ui.components.DoDepLoadingOverlay
import diftech.hackathon.ui.components.GlassButton
import diftech.hackathon.ui.components.GlassCard
import diftech.hackathon.ui.components.LiquidGlassBackground
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoDetailScreen(
    crypto: Crypto,
    repository: CryptoRepository = MockCryptoRepository(),
    onBackClick: () -> Unit
) {
    var recommendation by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    var isDoDepLoading by remember { mutableStateOf(false) }
    var showDoDepSuccess by remember { mutableStateOf(false) }

    LiquidGlassBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = crypto.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Назад",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 28.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = crypto.symbol,
                                fontSize = 18.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "$${String.format("%.2f", crypto.currentPrice)}",
                                fontSize = 38.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            val changeColor = if (crypto.priceChangePercent24h >= 0)
                                Color(0xFF5AF78E) else Color(0xFFFF6B7A)
                            val changePrefix = if (crypto.priceChangePercent24h >= 0) "+" else ""
                            Text(
                                text = "$changePrefix${String.format("%.2f", crypto.priceChangePercent24h)}%",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = changeColor
                            )
                        }
                    }

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 28.dp,
                        contentPadding = PaddingValues(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "График за 30 периодов",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            CryptoChart(
                                priceHistory = crypto.priceHistory,
                                lineColor = if (crypto.priceChangePercent24h >= 0)
                                    Color(0xFF5AF78E) else Color(0xFFFF6B7A)
                            )
                        }
                    }

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 28.dp,
                        contentPadding = PaddingValues(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Действия",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            GlassButton(
                                text = "Получить рекомендацию",
                                modifier = Modifier.fillMaxWidth(),
                                loading = isLoading,
                                enabled = !isLoading,
                                onClick = {
                                    isLoading = true
                                    scope.launch {
                                        recommendation = repository.getRecommendation(crypto)
                                        isLoading = false
                                    }
                                }
                            )
                            GlassButton(
                                text = "До-деп",
                                modifier = Modifier.fillMaxWidth(),
                                loading = isDoDepLoading,
                                enabled = !isDoDepLoading,
                                onClick = {
                                    if (!isDoDepLoading) {
                                        isDoDepLoading = true
                                        showDoDepSuccess = false
                                        scope.launch {
                                            delay(2200)
                                            isDoDepLoading = false
                                            showDoDepSuccess = true
                                        }
                                    }
                                }
                            )
                        }
                    }

                    if (recommendation != null) {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 28.dp,
                            contentPadding = PaddingValues(24.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Рекомендация",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = recommendation!!,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (recommendation == "ДО-ДЭП")
                                        Color(0xFF5AF78E) else Color(0xFFFFC86B)
                                )
                            }
                        }
                    }

                    if (showDoDepSuccess) {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 28.dp,
                            contentPadding = PaddingValues(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "До-деп выполнен",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Средства зачислены на счёт",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }

                if (isDoDepLoading) {
                    DoDepLoadingOverlay()
                }
            }
        }
    }
}

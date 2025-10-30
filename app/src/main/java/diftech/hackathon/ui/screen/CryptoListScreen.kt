package diftech.hackathon.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import diftech.hackathon.ui.components.GlassCard
import diftech.hackathon.ui.components.LiquidGlassBackground
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoListScreen(
    repository: CryptoRepository = MockCryptoRepository(),
    onCryptoClick: (Crypto) -> Unit
) {
    var cryptoList by remember { mutableStateOf<List<Crypto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            cryptoList = repository.getCryptoList()
            isLoading = false
        }
    }
    
    LiquidGlassBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Криптовалюты",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            }
        ) { padding ->
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White.copy(alpha = 0.8f))
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    items(cryptoList) { crypto ->
                        CryptoListItem(
                            crypto = crypto,
                            onClick = { onCryptoClick(crypto) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
fun CryptoListItem(
    crypto: Crypto,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = crypto.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = crypto.symbol,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${String.format("%.2f", crypto.currentPrice)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                val changeColor = if (crypto.priceChangePercent24h >= 0) Color(0xFF5AF78E) else Color(0xFFFF6B7A)
                val changePrefix = if (crypto.priceChangePercent24h >= 0) "+" else ""

                Text(
                    text = "$changePrefix${String.format("%.2f", crypto.priceChangePercent24h)}%",
                    fontSize = 14.sp,
                    color = changeColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

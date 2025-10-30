package diftech.hackathon.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoListScreen(
    repository: CryptoRepository = MockCryptoRepository(),
    onCryptoClick: (Crypto) -> Unit
) {
    val cryptoList by repository.cryptoListFlow.collectAsState()
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            repository.getCryptoList()
            isLoading = false
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Криптовалюты") }
            )
        }
    ) { padding ->
        if (isLoading && cryptoList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
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

@Composable
fun CryptoListItem(
    crypto: Crypto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = crypto.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = crypto.symbol,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${String.format("%.2f", crypto.currentPrice)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                
                val changeColor = if (crypto.priceChangePercent24h >= 0) Color(0xFF4CAF50) else Color(0xFFE53935)
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

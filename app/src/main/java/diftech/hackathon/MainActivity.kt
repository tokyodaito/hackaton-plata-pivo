package diftech.hackathon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import diftech.hackathon.data.model.Crypto
import diftech.hackathon.data.repository.MockCryptoRepository
import diftech.hackathon.ui.screen.CryptoDetailScreen
import diftech.hackathon.ui.screen.CryptoListScreen
import diftech.hackathon.ui.theme.PlataHackhathonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlataHackhathonTheme {
                CryptoApp()
            }
        }
    }
}

@Composable
fun CryptoApp() {
    var selectedCrypto by remember { mutableStateOf<Crypto?>(null) }
    val repository = remember { MockCryptoRepository() }
    
    if (selectedCrypto == null) {
        CryptoListScreen(
            repository = repository,
            onCryptoClick = { crypto ->
                selectedCrypto = crypto
            }
        )
    } else {
        CryptoDetailScreen(
            crypto = selectedCrypto!!,
            repository = repository,
            onBackClick = {
                selectedCrypto = null
            }
        )
    }
}
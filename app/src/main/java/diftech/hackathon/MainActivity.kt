package diftech.hackathon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import diftech.hackathon.data.model.Crypto
import diftech.hackathon.data.repository.RemoteCryptoRepository
import diftech.hackathon.ui.screen.CryptoDetailScreen
import diftech.hackathon.ui.screen.CryptoListScreen
import diftech.hackathon.ui.theme.PlataHackhathonTheme

class MainActivity : ComponentActivity() {
    
    private val repository = RemoteCryptoRepository()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlataHackhathonTheme {
                CryptoApp(repository)
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        repository.close()
    }
}

@Composable
fun CryptoApp(repository: RemoteCryptoRepository) {
    var selectedCrypto by remember { mutableStateOf<Crypto?>(null) }
    
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
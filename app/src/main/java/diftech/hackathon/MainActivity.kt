package diftech.hackathon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import diftech.hackathon.data.config.ApiConfig
import diftech.hackathon.data.model.Crypto
import diftech.hackathon.data.repository.CoinCapCryptoRepository
import diftech.hackathon.data.repository.CryptoCompareCryptoRepository
import diftech.hackathon.data.repository.CryptoRepository
import diftech.hackathon.data.repository.RemoteCryptoRepository
import diftech.hackathon.data.repository.RepositoryFactory
import diftech.hackathon.ui.screen.CryptoDetailScreen
import diftech.hackathon.ui.screen.CryptoListScreen
import diftech.hackathon.ui.theme.PlataHackhathonTheme

class MainActivity : ComponentActivity() {
    
    // Use factory to create repository
    // To change API - open RepositoryFactory.kt and change CURRENT_PROVIDER
    private val repository = RepositoryFactory.createCryptoRepository()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize configuration (load OpenAI API key)
        ApiConfig.init(applicationContext)
        
        // Start auto-refresh for data
        repository.startAutoRefresh()
        
        setContent {
            PlataHackhathonTheme {
                CryptoApp(repository)
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Close repository resources
        when (repository) {
            is RemoteCryptoRepository -> repository.close()
            is CoinCapCryptoRepository -> repository.close()
            is CryptoCompareCryptoRepository -> repository.close()
        }
    }
}

@Composable
fun CryptoApp(repository: CryptoRepository) {
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
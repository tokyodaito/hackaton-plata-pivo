package diftech.hackathon.data.repository

import diftech.hackathon.data.ai.CryptoAnalysisService
import diftech.hackathon.data.model.Crypto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class MockCryptoRepository : CryptoRepository {
    
    private val _cryptoListFlow = MutableStateFlow<List<Crypto>>(emptyList())
    override val cryptoListFlow: StateFlow<List<Crypto>> = _cryptoListFlow.asStateFlow()
    
    private val cryptoList = listOf(
        createMockCrypto("btc", "Bitcoin", "BTC", 67000.0),
        createMockCrypto("eth", "Ethereum", "ETH", 3400.0),
        createMockCrypto("bnb", "Binance Coin", "BNB", 580.0),
        createMockCrypto("sol", "Solana", "SOL", 145.0),
        createMockCrypto("xrp", "Ripple", "XRP", 0.52),
        createMockCrypto("ada", "Cardano", "ADA", 0.45),
        createMockCrypto("doge", "Dogecoin", "DOGE", 0.15),
        createMockCrypto("dot", "Polkadot", "DOT", 6.8),
        createMockCrypto("matic", "Polygon", "MATIC", 0.85),
        createMockCrypto("link", "Chainlink", "LINK", 14.5)
    )
    
    init {
        _cryptoListFlow.value = cryptoList
    }
    
    override suspend fun getCryptoList(): List<Crypto> {
        delay(300) // Simulate network request
        return cryptoList
    }
    
    override suspend fun getCryptoById(id: String): Crypto? {
        delay(200)
        return cryptoList.find { it.id == id }
    }
    
    override suspend fun getRecommendation(crypto: Crypto): CryptoAnalysisService.RecommendationResult {
        delay(500)
        val isBuy = Random.nextBoolean()
        return CryptoAnalysisService.RecommendationResult(
            shortRecommendation = if (isBuy) "BUY" else "DON'T TOUCH",
            detailedRecommendation = if (isBuy)
                "Mock analysis suggests positive market momentum with favorable technical indicators."
            else
                "Mock analysis indicates caution due to current market conditions and volatility."
        )
    }
    
    override fun startAutoRefresh() {
        // Mock repository doesn't need auto-refresh
    }
    
    override fun stopAutoRefresh() {
        // Mock repository doesn't need auto-refresh
    }
    
    private fun createMockCrypto(id: String, name: String, symbol: String, basePrice: Double): Crypto {
        val priceHistory = generatePriceHistory(basePrice, 30)
        val changePercent = ((priceHistory.last() - priceHistory.first()) / priceHistory.first()) * 100
        
        return Crypto(
            id = id,
            name = name,
            symbol = symbol,
            currentPrice = priceHistory.last(),
            priceChangePercent24h = changePercent,
            priceHistory = priceHistory
        )
    }
    
    private fun generatePriceHistory(basePrice: Double, points: Int): List<Double> {
        val history = mutableListOf<Double>()
        var currentPrice = basePrice * Random.nextDouble(0.9, 1.1)
        
        repeat(points) {
            history.add(currentPrice)
            currentPrice *= Random.nextDouble(0.95, 1.05)
        }
        
        return history
    }
}

package diftech.hackathon.data.repository

import diftech.hackathon.data.model.Crypto
import diftech.hackathon.data.remote.CoinGeckoApiService
import kotlin.random.Random

class RemoteCryptoRepository(
    private val apiService: CoinGeckoApiService = CoinGeckoApiService()
) : CryptoRepository {
    
    private var cachedCryptoList: List<Crypto> = emptyList()
    
    override suspend fun getCryptoList(): List<Crypto> {
        val marketData = apiService.getMarketData()
        
        cachedCryptoList = marketData.map { dto ->
            val priceHistory = dto.sparkline?.price?.takeLast(30) ?: emptyList()
            
            Crypto(
                id = dto.id,
                name = dto.name,
                symbol = dto.symbol.uppercase(),
                currentPrice = dto.currentPrice,
                priceChangePercent24h = dto.priceChangePercent24h ?: 0.0,
                priceHistory = if (priceHistory.isNotEmpty()) priceHistory else generateFallbackHistory(dto.currentPrice)
            )
        }
        
        return cachedCryptoList
    }
    
    override suspend fun getCryptoById(id: String): Crypto? {
        return cachedCryptoList.find { it.id == id }
    }
    
    override suspend fun getRecommendation(crypto: Crypto): String {
        // TODO: здесь будет реальная логика
        return if (Random.nextBoolean()) "ДО-ДЭП" else "Не трогать"
    }
    
    private fun generateFallbackHistory(basePrice: Double): List<Double> {
        val history = mutableListOf<Double>()
        var currentPrice = basePrice * Random.nextDouble(0.9, 1.0)
        
        repeat(30) {
            history.add(currentPrice)
            currentPrice *= Random.nextDouble(0.98, 1.02)
        }
        
        return history
    }
    
    fun close() {
        apiService.close()
    }
}

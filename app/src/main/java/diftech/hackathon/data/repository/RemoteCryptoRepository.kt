package diftech.hackathon.data.repository

import diftech.hackathon.data.model.Crypto
import diftech.hackathon.data.remote.CoinGeckoApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class RemoteCryptoRepository(
    private val apiService: CoinGeckoApiService = CoinGeckoApiService()
) : CryptoRepository {
    
    private val _cryptoListFlow = MutableStateFlow<List<Crypto>>(emptyList())
    override val cryptoListFlow: StateFlow<List<Crypto>> = _cryptoListFlow.asStateFlow()
    
    private val scope = CoroutineScope(Dispatchers.IO)
    private var refreshJob: Job? = null
    
    private val REFRESH_INTERVAL_MS = 30_000L // 30 секунд
    
    override suspend fun getCryptoList(): List<Crypto> {
        val marketData = apiService.getMarketData()
        
        val cryptoList = marketData.map { dto ->
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
        
        _cryptoListFlow.value = cryptoList
        return cryptoList
    }
    
    override suspend fun getCryptoById(id: String): Crypto? {
        return _cryptoListFlow.value.find { it.id == id }
    }
    
    override suspend fun getRecommendation(crypto: Crypto): String {
        // TODO: здесь будет реальная логика
        return if (Random.nextBoolean()) "ДО-ДЭП" else "Не трогать"
    }
    
    override fun startAutoRefresh() {
        if (refreshJob?.isActive == true) return
        
        refreshJob = scope.launch {
            while (true) {
                try {
                    getCryptoList()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(REFRESH_INTERVAL_MS)
            }
        }
    }
    
    override fun stopAutoRefresh() {
        refreshJob?.cancel()
        refreshJob = null
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
        stopAutoRefresh()
        apiService.close()
    }
}

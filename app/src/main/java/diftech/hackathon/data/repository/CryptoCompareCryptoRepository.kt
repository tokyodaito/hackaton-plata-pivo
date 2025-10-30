package diftech.hackathon.data.repository

import diftech.hackathon.data.model.Crypto
import diftech.hackathon.data.remote.CryptoCompareApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class CryptoCompareCryptoRepository(
    private val apiService: CryptoCompareApiService = CryptoCompareApiService()
) : CryptoRepository {
    
    private val _cryptoListFlow = MutableStateFlow<List<Crypto>>(emptyList())
    override val cryptoListFlow: StateFlow<List<Crypto>> = _cryptoListFlow.asStateFlow()
    
    private val scope = CoroutineScope(Dispatchers.IO)
    private var refreshJob: Job? = null
    
    private val REFRESH_INTERVAL_MS = 30_000L // 30 секунд
    
    override suspend fun getCryptoList(): List<Crypto> {
        val response = apiService.getMultiPrice()
        
        val cryptoList = response?.raw?.map { (symbol, usdData) ->
            val data = usdData.usd
            val price = data.price
            val changePercent = data.changePercent24Hour
            
            // Генерируем историю цен
            val priceHistory = generatePriceHistory(price, changePercent)
            
            Crypto(
                id = symbol.lowercase(),
                name = apiService.getCryptoName(symbol),
                symbol = symbol,
                currentPrice = price,
                priceChangePercent24h = changePercent,
                priceHistory = priceHistory
            )
        } ?: emptyList()
        
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
    
    private fun generatePriceHistory(currentPrice: Double, changePercent24h: Double): List<Double> {
        val history = mutableListOf<Double>()
        
        // Начальная цена 24 часа назад
        val startPrice = currentPrice / (1 + changePercent24h / 100)
        val priceRange = currentPrice - startPrice
        
        // Генерируем 30 точек от начальной до текущей цены
        for (i in 0..29) {
            val progress = i / 29.0
            val randomFactor = Random.nextDouble(0.98, 1.02)
            val price = (startPrice + priceRange * progress) * randomFactor
            history.add(price)
        }
        
        return history
    }
    
    fun close() {
        stopAutoRefresh()
        apiService.close()
    }
}

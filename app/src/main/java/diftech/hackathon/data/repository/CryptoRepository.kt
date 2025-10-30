package diftech.hackathon.data.repository

import diftech.hackathon.data.ai.CryptoAnalysisService
import diftech.hackathon.data.model.Crypto
import kotlinx.coroutines.flow.StateFlow

interface CryptoRepository {
    val cryptoListFlow: StateFlow<List<Crypto>>
    suspend fun getCryptoList(): List<Crypto>
    suspend fun getCryptoById(id: String): Crypto?
    suspend fun getRecommendation(crypto: Crypto): CryptoAnalysisService.RecommendationResult
    fun startAutoRefresh()
    fun stopAutoRefresh()
}

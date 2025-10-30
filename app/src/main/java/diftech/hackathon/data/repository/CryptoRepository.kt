package diftech.hackathon.data.repository

import diftech.hackathon.data.model.Crypto

interface CryptoRepository {
    suspend fun getCryptoList(): List<Crypto>
    suspend fun getCryptoById(id: String): Crypto?
    suspend fun getRecommendation(crypto: Crypto): String
}

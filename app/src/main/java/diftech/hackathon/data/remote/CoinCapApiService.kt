package diftech.hackathon.data.remote

import diftech.hackathon.data.remote.dto.CoinCapAssetsResponse
import diftech.hackathon.data.remote.dto.CoinCapHistoryResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class CoinCapApiService {
    
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            })
        }
        
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }
    
    private val baseUrl = "https://api.coincap.io/v2"
    
    // Топ 10 криптовалют по капитализации
    private val topCryptoIds = listOf(
        "bitcoin",
        "ethereum",
        "tether",
        "binance-coin",
        "solana",
        "ripple",
        "usd-coin",
        "cardano",
        "dogecoin",
        "tron"
    )
    
    suspend fun getAssets(): CoinCapAssetsResponse? {
        return try {
            val ids = topCryptoIds.joinToString(",")
            client.get("$baseUrl/assets") {
                parameter("ids", ids)
            }.body()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    suspend fun getAssetHistory(coinId: String, interval: String = "h1"): CoinCapHistoryResponse? {
        return try {
            client.get("$baseUrl/assets/$coinId/history") {
                parameter("interval", interval)
            }.body()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun close() {
        client.close()
    }
}

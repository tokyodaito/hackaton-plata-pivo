package diftech.hackathon.data.remote

import diftech.hackathon.data.remote.dto.CoinMarketDto
import diftech.hackathon.data.remote.dto.MarketChartDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class CoinGeckoApiService {
    
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
    
    private val baseUrl = "https://api.coingecko.com/api/v3"
    
    // Топ 10 криптовалют по капитализации
    private val topCryptoIds = listOf(
        "bitcoin",
        "ethereum", 
        "tether",
        "binancecoin",
        "solana",
        "ripple",
        "usd-coin",
        "cardano",
        "dogecoin",
        "tron"
    )
    
    suspend fun getMarketData(): List<CoinMarketDto> {
        return try {
            val ids = topCryptoIds.joinToString(",")
            client.get("$baseUrl/coins/markets") {
                parameter("vs_currency", "usd")
                parameter("ids", ids)
                parameter("order", "market_cap_desc")
                parameter("per_page", 10)
                parameter("page", 1)
                parameter("sparkline", true)
                parameter("price_change_percentage", "24h")
            }.body()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    suspend fun getMarketChart(coinId: String, days: Int = 1): MarketChartDto? {
        return try {
            client.get("$baseUrl/coins/$coinId/market_chart") {
                parameter("vs_currency", "usd")
                parameter("days", days)
                parameter("interval", "hourly")
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

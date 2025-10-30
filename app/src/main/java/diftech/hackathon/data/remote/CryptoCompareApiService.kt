package diftech.hackathon.data.remote

import diftech.hackathon.data.remote.dto.CryptoCompareMultiResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class CryptoCompareApiService {
    
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
    
    private val baseUrl = "https://min-api.cryptocompare.com/data"
    
    // Топ 10 криптовалют
    private val topCryptoSymbols = listOf(
        "BTC",
        "ETH",
        "USDT",
        "BNB",
        "SOL",
        "XRP",
        "USDC",
        "ADA",
        "DOGE",
        "TRX"
    )
    
    private val cryptoNames = mapOf(
        "BTC" to "Bitcoin",
        "ETH" to "Ethereum",
        "USDT" to "Tether",
        "BNB" to "Binance Coin",
        "SOL" to "Solana",
        "XRP" to "Ripple",
        "USDC" to "USD Coin",
        "ADA" to "Cardano",
        "DOGE" to "Dogecoin",
        "TRX" to "Tron"
    )
    
    suspend fun getMultiPrice(): CryptoCompareMultiResponse? {
        return try {
            val symbols = topCryptoSymbols.joinToString(",")
            client.get("$baseUrl/pricemultifull") {
                parameter("fsyms", symbols)
                parameter("tsyms", "USD")
            }.body()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun getCryptoName(symbol: String): String {
        return cryptoNames[symbol] ?: symbol
    }
    
    fun close() {
        client.close()
    }
}

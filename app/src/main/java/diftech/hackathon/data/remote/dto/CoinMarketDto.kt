package diftech.hackathon.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinMarketDto(
    @SerialName("id")
    val id: String,
    
    @SerialName("symbol")
    val symbol: String,
    
    @SerialName("name")
    val name: String,
    
    @SerialName("current_price")
    val currentPrice: Double,
    
    @SerialName("price_change_percentage_24h")
    val priceChangePercent24h: Double? = null,
    
    @SerialName("sparkline_in_7d")
    val sparkline: SparklineDto? = null
)

@Serializable
data class SparklineDto(
    @SerialName("price")
    val price: List<Double>
)

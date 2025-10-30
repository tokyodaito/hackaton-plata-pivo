package diftech.hackathon.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoinCapAssetsResponse(
    @SerialName("data")
    val data: List<CoinCapAssetDto>
)

@Serializable
data class CoinCapAssetDto(
    @SerialName("id")
    val id: String,
    
    @SerialName("symbol")
    val symbol: String,
    
    @SerialName("name")
    val name: String,
    
    @SerialName("priceUsd")
    val priceUsd: String,
    
    @SerialName("changePercent24Hr")
    val changePercent24Hr: String? = null
)

@Serializable
data class CoinCapHistoryResponse(
    @SerialName("data")
    val data: List<CoinCapHistoryDto>
)

@Serializable
data class CoinCapHistoryDto(
    @SerialName("priceUsd")
    val priceUsd: String,
    
    @SerialName("time")
    val time: Long
)

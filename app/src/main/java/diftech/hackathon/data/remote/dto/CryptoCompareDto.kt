package diftech.hackathon.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CryptoCompareMultiResponse(
    @SerialName("RAW")
    val raw: Map<String, CryptoCompareUSDData>
)

@Serializable
data class CryptoCompareUSDData(
    @SerialName("USD")
    val usd: CryptoCompareData
)

@Serializable
data class CryptoCompareData(
    @SerialName("PRICE")
    val price: Double,
    
    @SerialName("CHANGEPCT24HOUR")
    val changePercent24Hour: Double,
    
    @SerialName("FROMSYMBOL")
    val fromSymbol: String
)

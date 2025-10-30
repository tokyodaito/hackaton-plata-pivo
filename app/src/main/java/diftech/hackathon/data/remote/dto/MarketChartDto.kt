package diftech.hackathon.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MarketChartDto(
    @SerialName("prices")
    val prices: List<List<Double>>
)

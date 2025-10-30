package diftech.hackathon.data.model

data class Crypto(
    val id: String,
    val name: String,
    val symbol: String,
    val currentPrice: Double,
    val priceChangePercent24h: Double,
    val priceHistory: List<Double>,
    val aiRecommendation: String? = null,
    val aiRecommendationDetails: String? = null
)

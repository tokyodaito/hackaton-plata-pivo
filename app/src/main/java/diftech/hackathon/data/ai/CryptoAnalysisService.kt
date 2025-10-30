package diftech.hackathon.data.ai

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import android.content.Context
import android.util.Log
import diftech.hackathon.data.config.ApiConfig
import diftech.hackathon.data.model.Crypto

/**
 * Service for analyzing cryptocurrency using Koog AI Agent Framework
 * Provides buy/sell recommendations based on current market data
 */
class CryptoAnalysisService(private val context: Context? = null) {

    private val systemPrompt = """
        You are a cryptocurrency expert and financial analyst.
        Your task is to provide buy/sell recommendations for cryptocurrencies.

        Your response MUST follow this exact format:
        RECOMMENDATION: [BUY or DON'T TOUCH]
        DETAILS: [2-3 sentences explaining the reasoning behind your recommendation, including market trends, technical indicators, and key factors]

        Example:
        RECOMMENDATION: BUY
        DETAILS: Bitcoin is showing strong bullish momentum with increasing institutional adoption. The recent price consolidation above $60K indicates solid support levels. Major analysts predict continued growth in Q4 2024.

        Always start with "RECOMMENDATION:" followed by either "BUY" or "DON'T TOUCH", then on a new line "DETAILS:" with your analysis.
    """.trimIndent()

    /**
     * Create a new AIAgent instance for each request
     * This prevents "Agent was already started" errors
     */
    private fun createAgent(): AIAgent<String, String> {
        return AIAgent(
            promptExecutor = simpleOpenAIExecutor(ApiConfig.getOpenAiKey()),
            systemPrompt = systemPrompt,
            llmModel = OpenAIModels.Chat.GPT4o
        )
    }
    
    data class RecommendationResult(
        val shortRecommendation: String,
        val detailedRecommendation: String
    )

    /**
     * Get AI-powered recommendation for a cryptocurrency
     * @param crypto The cryptocurrency to analyze
     * @return RecommendationResult with short and detailed recommendations
     */
    suspend fun getRecommendation(crypto: Crypto): RecommendationResult {
        if (!ApiConfig.isOpenAiKeyConfigured()) {
            return RecommendationResult(
                shortRecommendation = "⚠️ API not configured",
                detailedRecommendation = "OpenAI API not configured. Add key to config.properties"
            )
        }

        return try {
            val prompt = loadPromptTemplate(crypto)
            Log.d(TAG, "📤 Sending prompt to Koog AI:\n$prompt")

            // Create a fresh agent for each request to avoid "Agent was already started" error
            val agent = createAgent()
            val response = agent.run(prompt)
            Log.d(TAG, "📥 Koog AI response:\n$response")

            // Parse the response
            val recommendationLine = response.lines()
                .firstOrNull { it.startsWith("RECOMMENDATION:", ignoreCase = true) }
                ?.substringAfter("RECOMMENDATION:", "")
                ?.trim() ?: ""

            val detailsLine = response.lines()
                .firstOrNull { it.startsWith("DETAILS:", ignoreCase = true) }
                ?.substringAfter("DETAILS:", "")
                ?.trim() ?: response

            // Normalize short recommendation
            val shortRec = when {
                recommendationLine.contains("BUY", ignoreCase = true) -> "BUY"
                else -> "DON'T TOUCH"
            }

            Log.d(TAG, "✅ Short: $shortRec | Details: $detailsLine")

            RecommendationResult(
                shortRecommendation = shortRec,
                detailedRecommendation = detailsLine
            )

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting recommendation", e)
            e.printStackTrace()
            RecommendationResult(
                shortRecommendation = "❌ Error",
                detailedRecommendation = e.message ?: "Unknown error occurred"
            )
        }
    }

    companion object {
        private const val TAG = "CryptoAnalysisService"
    }
    
    /**
     * Load and populate prompt template with crypto data
     */
    private fun loadPromptTemplate(crypto: Crypto): String {
        val template = try {
            context?.resources?.openRawResource(
                context.resources.getIdentifier("crypto_analysis_prompt", "raw", context.packageName)
            )?.bufferedReader()?.use { it.readText() }
        } catch (e: Exception) {
            null
        } ?: getDefaultPromptTemplate()
        
        val trend = when {
            crypto.priceChangePercent24h > 5 -> "strong growth (+${String.format("%.2f", crypto.priceChangePercent24h)}%)"
            crypto.priceChangePercent24h > 0 -> "slight growth (+${String.format("%.2f", crypto.priceChangePercent24h)}%)"
            crypto.priceChangePercent24h > -5 -> "slight decline (${String.format("%.2f", crypto.priceChangePercent24h)}%)"
            else -> "strong decline (${String.format("%.2f", crypto.priceChangePercent24h)}%)"
        }
        
        val priceLevel = when {
            crypto.currentPrice >= 10000 -> "high price"
            crypto.currentPrice >= 100 -> "medium price"
            crypto.currentPrice >= 1 -> "low price"
            else -> "very low price"
        }
        
        val dynamics = if (crypto.priceChangePercent24h > 0) "positive" else "negative"
        
        return template
            .replace("{CRYPTO_NAME}", crypto.name)
            .replace("{CRYPTO_SYMBOL}", crypto.symbol)
            .replace("{CURRENT_PRICE}", String.format("%.2f", crypto.currentPrice))
            .replace("{PRICE_LEVEL}", priceLevel)
            .replace("{TREND}", trend)
            .replace("{DYNAMICS}", dynamics)
    }
    
    /**
     * Default prompt template if file not found
     */
    private fun getDefaultPromptTemplate(): String {
        return """
            Analyze the cryptocurrency {CRYPTO_NAME} ({CRYPTO_SYMBOL}) and provide a recommendation.
            
            Current data:
            - Price: ${'$'}{CURRENT_PRICE} ({PRICE_LEVEL})
            - 24-hour change: {TREND}
            - Dynamics: {DYNAMICS}
            
            Consider:
            1. Current news and market sentiment regarding {CRYPTO_NAME}
            2. Recommendations from major brokers and analysts
            3. Technical analysis and trends
            4. Fundamental indicators of the project
            5. Overall state of the cryptocurrency market
            
            Provide a recommendation: buy ("BUY NOW") or don't buy ("DON'T TOUCH")?
        """.trimIndent()
    }
    
    fun close() {
        // Koog AIAgent doesn't require explicit closing
    }
}

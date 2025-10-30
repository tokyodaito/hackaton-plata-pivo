package diftech.hackathon.data.ai

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import android.content.Context
import diftech.hackathon.data.config.ApiConfig
import diftech.hackathon.data.model.Crypto

/**
 * Service for analyzing cryptocurrency using Koog AI Agent Framework
 * Provides buy/sell recommendations based on current market data
 */
class CryptoAnalysisService(private val context: Context? = null) {

    private var agent: AIAgent<String, String>? = null

    init {
        if (ApiConfig.isOpenAiKeyConfigured()) {
            agent = AIAgent(
                promptExecutor = simpleOpenAIExecutor(ApiConfig.getOpenAiKey()),
                systemPrompt = """
                    You are a cryptocurrency expert and financial analyst.
                    Your task is to provide brief buy/sell recommendations for cryptocurrencies.

                    Respond STRICTLY with one of two options:
                    - "BUY NOW" - if you recommend buying
                    - "DON'T TOUCH" - if you DON'T recommend buying (sell or stay away)

                    Your response must contain ONLY one of these phrases, without additional explanations.
                """.trimIndent(),
                llmModel = OpenAIModels.Chat.GPT4o
            )
        }
    }
    
    /**
     * Get AI-powered recommendation for a cryptocurrency
     * @param crypto The cryptocurrency to analyze
     * @return "BUY NOW" or "DON'T TOUCH"
     */
    suspend fun getRecommendation(crypto: Crypto): String {
        if (agent == null) {
            return "⚠️ OpenAI API not configured. Add key to config.properties"
        }

        return try {
            val prompt = loadPromptTemplate(crypto)
            val response = agent!!.run(prompt)

            // Normalize response
            when {
                response.contains("BUY NOW", ignoreCase = true) ||
                response.contains("BUY", ignoreCase = true) -> "BUY NOW"
                else -> "DON'T TOUCH"
            }

        } catch (e: Exception) {
            e.printStackTrace()
            "❌ Error: ${e.message}"
        }
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

package diftech.hackathon.data.ai

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import diftech.hackathon.data.config.ApiConfig
import diftech.hackathon.data.model.Crypto

class CryptoAnalysisService {
    
    private var openAI: OpenAI? = null
    
    init {
        if (ApiConfig.isOpenAiKeyConfigured()) {
            openAI = OpenAI(ApiConfig.getOpenAiKey())
        }
    }
    
    suspend fun getRecommendation(crypto: Crypto): String {
        if (openAI == null) {
            return "⚠️ OpenAI API не настроен. Добавьте ключ в config.properties"
        }
        
        return try {
            val prompt = buildCryptoAnalysisPrompt(crypto)
            
            val chatCompletionRequest = ChatCompletionRequest(
                model = ModelId("gpt-4o-mini"),
                messages = listOf(
                    ChatMessage(
                        role = ChatRole.System,
                        content = """
                            Ты - эксперт по криптовалютам и финансовый аналитик. 
                            Твоя задача - давать краткие рекомендации по покупке или продаже криптовалюты.
                            
                            Отвечай СТРОГО одним из двух вариантов:
                            - "ДО-ДЭП" - если рекомендуешь покупать
                            - "Не трогать" - если НЕ рекомендуешь покупать (продавать или держать в стороне)
                            
                            Твой ответ должен содержать ТОЛЬКО одну из этих фраз, без дополнительных объяснений.
                        """.trimIndent()
                    ),
                    ChatMessage(
                        role = ChatRole.User,
                        content = prompt
                    )
                ),
                temperature = 0.7,
                maxTokens = 50
            )
            
            val completion: ChatCompletion = openAI!!.chatCompletion(chatCompletionRequest)
            val response = completion.choices.firstOrNull()?.message?.content ?: "Не трогать"
            
            // Нормализуем ответ
            when {
                response.contains("ДО-ДЭП", ignoreCase = true) -> "ДО-ДЭП"
                else -> "Не трогать"
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
            "❌ Ошибка: ${e.message}"
        }
    }
    
    private fun buildCryptoAnalysisPrompt(crypto: Crypto): String {
        val trend = when {
            crypto.priceChangePercent24h > 5 -> "сильный рост (+${String.format("%.2f", crypto.priceChangePercent24h)}%)"
            crypto.priceChangePercent24h > 0 -> "небольшой рост (+${String.format("%.2f", crypto.priceChangePercent24h)}%)"
            crypto.priceChangePercent24h > -5 -> "небольшое падение (${String.format("%.2f", crypto.priceChangePercent24h)}%)"
            else -> "сильное падение (${String.format("%.2f", crypto.priceChangePercent24h)}%)"
        }
        
        val priceLevel = when {
            crypto.currentPrice >= 10000 -> "высокая стоимость"
            crypto.currentPrice >= 100 -> "средняя стоимость"
            crypto.currentPrice >= 1 -> "низкая стоимость"
            else -> "очень низкая стоимость"
        }
        
        return """
            Проанализируй криптовалюту ${crypto.name} (${crypto.symbol}) и дай рекомендацию.
            
            Текущие данные:
            - Цена: $${String.format("%.2f", crypto.currentPrice)} (${priceLevel})
            - Изменение за 24 часа: ${trend}
            - Динамика: ${if (crypto.priceChangePercent24h > 0) "положительная" else "отрицательная"}
            
            Учитывай:
            1. Текущие новости и настроения на крипторынке относительно ${crypto.name}
            2. Рекомендации крупных брокеров и аналитиков
            3. Технический анализ и тренды
            4. Фундаментальные показатели проекта
            5. Общее состояние криптовалютного рынка
            
            Дай рекомендацию: покупать ("ДО-ДЭП") или не покупать ("Не трогать")?
        """.trimIndent()
    }
    
    fun close() {
        // OpenAI client не требует явного закрытия
    }
}

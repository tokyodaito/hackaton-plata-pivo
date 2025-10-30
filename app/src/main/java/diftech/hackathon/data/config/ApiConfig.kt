package diftech.hackathon.data.config

import android.content.Context
import java.util.Properties

object ApiConfig {
    private var openAiKey: String? = null
    
    fun init(context: Context) {
        try {
            val properties = Properties()
            context.resources.openRawResource(
                context.resources.getIdentifier("config", "raw", context.packageName)
            ).use { inputStream ->
                properties.load(inputStream)
            }
            openAiKey = properties.getProperty("openai.api.key")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun getOpenAiKey(): String {
        return openAiKey ?: "YOUR_OPENAI_API_KEY_HERE"
    }
    
    fun isOpenAiKeyConfigured(): Boolean {
        return openAiKey != null && openAiKey != "YOUR_OPENAI_API_KEY_HERE"
    }
}

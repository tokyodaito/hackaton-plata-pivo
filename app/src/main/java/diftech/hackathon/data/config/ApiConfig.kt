package diftech.hackathon.data.config

import android.content.Context
import java.util.Properties

/**
 * Configuration manager for API keys and settings
 * Loads configuration from res/raw/config.properties
 */
object ApiConfig {
    private var openAiKey: String? = null
    private var appContext: Context? = null
    
    /**
     * Initialize configuration by loading from properties file
     * @param context Application context
     */
    fun init(context: Context) {
        appContext = context.applicationContext
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
    
    /**
     * Get OpenAI API key
     * @return API key or placeholder if not configured
     */
    fun getOpenAiKey(): String {
        return openAiKey ?: "YOUR_OPENAI_API_KEY_HERE"
    }
    
    /**
     * Check if OpenAI API key is properly configured
     * @return true if key is set and valid
     */
    fun isOpenAiKeyConfigured(): Boolean {
        return openAiKey != null && openAiKey != "YOUR_OPENAI_API_KEY_HERE"
    }
    
    /**
     * Get application context
     * @return Application context or null if not initialized
     */
    fun getContext(): Context? = appContext
}

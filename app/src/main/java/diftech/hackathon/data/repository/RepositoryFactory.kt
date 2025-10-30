package diftech.hackathon.data.repository

enum class ApiProvider {
    COIN_GECKO,
    COIN_CAP,
    CRYPTO_COMPARE,
    MOCK
}

object RepositoryFactory {
    
    // ===== ИЗМЕНИ ЗДЕСЬ API PROVIDER =====
    // Доступные варианты: COIN_GECKO, COIN_CAP, CRYPTO_COMPARE, MOCK
    // CRYPTO_COMPARE - самый надежный, работает без регистрации
    private val CURRENT_PROVIDER = ApiProvider.CRYPTO_COMPARE
    // =====================================
    
    fun createCryptoRepository(): CryptoRepository {
        return when (CURRENT_PROVIDER) {
            ApiProvider.COIN_GECKO -> RemoteCryptoRepository()
            ApiProvider.COIN_CAP -> CoinCapCryptoRepository()
            ApiProvider.CRYPTO_COMPARE -> CryptoCompareCryptoRepository()
            ApiProvider.MOCK -> MockCryptoRepository()
        }
    }
    
    fun getCurrentProvider(): ApiProvider = CURRENT_PROVIDER
}

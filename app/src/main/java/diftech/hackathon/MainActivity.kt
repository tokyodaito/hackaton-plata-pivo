package diftech.hackathon

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import diftech.hackathon.data.model.Crypto
import diftech.hackathon.data.repository.CoinCapCryptoRepository
import diftech.hackathon.data.repository.CryptoCompareCryptoRepository
import diftech.hackathon.data.repository.CryptoRepository
import diftech.hackathon.data.repository.RemoteCryptoRepository
import diftech.hackathon.data.repository.RepositoryFactory
import diftech.hackathon.ui.components.BeerOverlay
import diftech.hackathon.ui.screen.CryptoDetailScreen
import diftech.hackathon.ui.screen.CryptoListScreen
import diftech.hackathon.ui.theme.PlataHackhathonTheme
import kotlin.math.sqrt

class MainActivity : ComponentActivity(), SensorEventListener {

    // Используем фабрику для создания репозитория
    // Чтобы изменить API - открой RepositoryFactory.kt и измени CURRENT_PROVIDER
    private val repository = RepositoryFactory.createCryptoRepository()
    private val beerOverlayState = mutableStateOf(false)
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastShakeTimestamp = 0L
    private val shakeThresholdGravity = 2.7f
    private val shakeSlopTimeMs = 800

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Запускаем автообновление данных
        repository.startAutoRefresh()

        setContent {
            PlataHackhathonTheme {
                val showBeerOverlay by beerOverlayState
                CryptoApp(
                    repository = repository,
                    showBeerOverlay = showBeerOverlay,
                    onBeerOverlayFinished = { beerOverlayState.value = false },
                    onBeerOverlayRequested = ::triggerBeerOverlay
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        sensorManager.unregisterListener(this)
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Закрываем ресурсы репозитория
        when (repository) {
            is RemoteCryptoRepository -> repository.close()
            is CoinCapCryptoRepository -> repository.close()
            is CryptoCompareCryptoRepository -> repository.close()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val gX = x / SensorManager.GRAVITY_EARTH
        val gY = y / SensorManager.GRAVITY_EARTH
        val gZ = z / SensorManager.GRAVITY_EARTH

        val gForce = sqrt(gX * gX + gY * gY + gZ * gZ)

        if (gForce > shakeThresholdGravity) {
            val now = SystemClock.elapsedRealtime()
            if (lastShakeTimestamp + shakeSlopTimeMs < now) {
                lastShakeTimestamp = now
                triggerBeerOverlay()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    private fun triggerBeerOverlay() {
        runOnUiThread {
            if (beerOverlayState.value) {
                beerOverlayState.value = false
                window.decorView.post { beerOverlayState.value = true }
            } else {
                beerOverlayState.value = true
            }
        }
    }
}

@Composable
fun CryptoApp(
    repository: CryptoRepository,
    showBeerOverlay: Boolean,
    onBeerOverlayFinished: () -> Unit,
    onBeerOverlayRequested: () -> Unit
) {
    var selectedCrypto by remember { mutableStateOf<Crypto?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (selectedCrypto == null) {
            CryptoListScreen(
                repository = repository,
                onCryptoClick = { crypto ->
                    selectedCrypto = crypto
                },
                onBeerOverlayRequested = onBeerOverlayRequested
            )
        } else {
            CryptoDetailScreen(
                crypto = selectedCrypto!!,
                repository = repository,
                onBackClick = {
                    selectedCrypto = null
                }
            )
        }

        if (showBeerOverlay) {
            BeerOverlay(onDismiss = onBeerOverlayFinished)
        }
    }
}

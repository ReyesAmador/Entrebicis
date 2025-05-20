package cat.copernic.p3grup1.entrebicis.core.location

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import cat.copernic.p3grup1.entrebicis.core.network.RetrofitClient
import cat.copernic.p3grup1.entrebicis.core.utils.LogRutaUtils
import cat.copernic.p3grup1.entrebicis.route.data.repositories.RouteRepo
import cat.copernic.p3grup1.entrebicis.route.data.sources.remote.PuntGpsDto
import cat.copernic.p3grup1.entrebicis.route.data.sources.remote.RouteApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Servei en segon pla encarregat de capturar la localització GPS de l’usuari
 * i enviar-la periòdicament al backend mentre una ruta està activa.
 * Finalitza automàticament la ruta si detecta inactivitat prolongada.
 */
@RequiresApi(Build.VERSION_CODES.O)
class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var routeRepo: RouteRepo

    private var tempsMaximAturadaMillis: Long = 0L
    private var lastMovementTimestamp: Long = System.currentTimeMillis()
    private var rutaActiva: Boolean = true
    private var lastKnownLocation: Location = Location("init").apply {
        latitude = 0.0
        longitude = 0.0
    }
    private var intervalUpdateMs: Long = 5000L
    private var distanciaMinima: Float = 10f
    private var pointsDiscarded = 0

    private val handler = Handler(Looper.getMainLooper())
    private val checkInactivityRunnable = object : Runnable {
        override fun run() {
            if (rutaActiva) {
                val currentTime = System.currentTimeMillis()
                if ((currentTime - lastMovementTimestamp) > tempsMaximAturadaMillis) {
                    finalizarRutaPerInactivitat()
                } else {
                    handler.postDelayed(this, 5_000L)
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    /**
     * S'executa quan el servei es crea per primera vegada.
     * Inicialitza els components de localització, canal de notificació
     * i comença a escoltar ubicacions després d'obtenir els paràmetres del backend.
     */
    override fun onCreate() {
        super.onCreate()

        // Inicializar repo y cliente
        routeRepo = RouteRepo(RetrofitClient.getInstance(applicationContext).create(RouteApi::class.java))
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        createNotificationChannel()
        startForeground(1, buildNotification())

        LogRutaUtils.appendLog(applicationContext, "Ruta iniciada automàticament pel sistema")

        // Inicializamos el callback de ubicación
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (location in result.locations) {
                    handleNewLocation(location)
                }
            }
        }

        // 🔁 Obtener parámetro "temps_maxim_aturada" antes de iniciar ubicación
        CoroutineScope(Dispatchers.IO).launch {
            routeRepo.getTempsMaximAturada().onSuccess { minuts ->
                tempsMaximAturadaMillis = minuts * 60_000L

                // 🔄 Obtenir paràmetres GPS
                routeRepo.getGpsParams().onSuccess { (interval, dist) ->
                    intervalUpdateMs = interval
                    distanciaMinima = dist
                    Log.d("GPS_PARAM", "Interval: $interval ms, Distància mínima: $dist m")
                }.onFailure {
                    Log.w("GPS_PARAM", "Error obtenint paràmetres GPS, usant per defecte")
                }
                startLocationUpdates() // solo iniciar si tenemos el valor
                handler.postDelayed(checkInactivityRunnable, 5_000L)
                Log.d("RUTA", "Temps màxim aturat rebut: $minuts minuts")
            }.onFailure {
                Log.e("RUTA", "❌ No s'ha pogut obtenir el paràmetre de temps màxim. Servei detingut.")
                stopSelf() // cancelamos si no se puede obtener
            }
        }
    }

    /**
     * Inicia les actualitzacions de localització segons els paràmetres configurats
     * de precisió i interval. Requereix permisos de localització atorgats.
     */
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            intervalUpdateMs
        ).build()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    /**
     * Gestiona cada nova localització rebuda pel sistema.
     * Ignora ubicacions amb baixa precisió o punts inicials, i envia només
     * aquells que superen la distància mínima configurada.
     *
     * @param location Ubicació GPS recent detectada.
     */
    private fun handleNewLocation(location: Location) {
        if (location.accuracy > 20) {
            Log.d("GPS", "Ignorant punt per baixa precisió: ${location.accuracy}m")
            return
        }

        if (pointsDiscarded < 3) {
            pointsDiscarded++
            Log.d("GPS", "Ignorant punt inicial $pointsDiscarded")
            return
        }

        val currentTime = System.currentTimeMillis()
        val distanceMoved = location.distanceTo(lastKnownLocation)

        // Si se ha movido más de 10m, se considera movimiento
        if (distanceMoved > distanciaMinima) {
            lastMovementTimestamp = currentTime
            val punt = PuntGpsDto(
                latitud = location.latitude,
                longitud = location.longitude,
                temps = currentTime
            )
            CoroutineScope(Dispatchers.IO).launch {
                routeRepo.enviarPunt(punt)
            }
        }
        lastKnownLocation = location
    }

    /**
     * Finalitza automàticament la ruta per inactivitat (temps sense moviment superat),
     * notifica al backend i atura el servei.
     */
    private fun finalizarRutaPerInactivitat() {
        rutaActiva = false
        Log.d("RUTA", "Ruta finalitzada automàticament per aturada. Temps quiet superat.")
        LogRutaUtils.appendLog(applicationContext, "Ruta finalitzada automàticament per aturada. Temps quiet superat.")

        CoroutineScope(Dispatchers.IO).launch {
            routeRepo.finalitzarRuta()
            Log.d("RUTA", "Ruta finalitzada al backend amb èxit")
        }

        sendFinalizationBroadcast()
        stopSelf()
    }

    /**
     * Crea una notificació persistent que mostra que la ruta està en marxa.
     * És necessària per mantenir el servei actiu en primer pla.
     *
     * @return Notificació construïda per mostrar a l’usuari.
     */
    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, "route_channel")
            .setContentTitle("Ruta en marxa")
            .setContentText("Capturant localització cada 10 segons")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .build()
    }

    /**
     * Crea el canal de notificació necessari per mostrar notificacions
     * a dispositius amb Android O o superior.
     */
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "route_channel",
            "Seguiment de ruta",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    /**
     * S'executa quan el servei és destruït, per exemple, quan la ruta finalitza o l'app es tanca.
     * Finalitza la ruta si encara està activa i allibera recursos.
     */
    override fun onDestroy() {
        super.onDestroy()
        if (rutaActiva) {
            Log.d("LocationService", "🔥 App cerrada: finalitzant ruta activa")
            finalizarRutaPerInactivitat()
        }
        rutaActiva = false
        handler.removeCallbacks(checkInactivityRunnable)
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d("LocationService", "Servicio detenido (parada o tiempo excedido)")
    }

    /**
     * Envia un broadcast local per notificar que la ruta ha estat finalitzada automàticament,
     * útil per alertar el ViewModel o la pantalla principal.
     */
    private fun sendFinalizationBroadcast() {
        val intent = Intent("com.entrebicis.RUTA_FINALITZADA_AUTO")
        intent.setPackage(packageName)
        intent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
        Log.d("LOCATION_SERVICE", "📡 Broadcast enviat amb package: $packageName")
        sendBroadcast(intent)
    }
}
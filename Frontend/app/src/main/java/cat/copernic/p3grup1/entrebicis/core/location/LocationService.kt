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
                startLocationUpdates() // solo iniciar si tenemos el valor
                handler.postDelayed(checkInactivityRunnable, 5_000L)
                Log.d("RUTA", "Temps màxim aturat rebut: $minuts minuts")
            }.onFailure {
                Log.e("RUTA", "❌ No s'ha pogut obtenir el paràmetre de temps màxim. Servei detingut.")
                stopSelf() // cancelamos si no se puede obtener
            }
        }
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2_000L // cada 10s
        ).setMinUpdateDistanceMeters(5f).build()

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

    private fun handleNewLocation(location: Location) {

        val currentTime = System.currentTimeMillis()
        val distanceMoved = location.distanceTo(lastKnownLocation)

        // Si se ha movido más de 5m, se considera movimiento
        if (distanceMoved > 5f) {
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

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, "route_channel")
            .setContentTitle("Ruta en marxa")
            .setContentText("Capturant localització cada 10 segons")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "route_channel",
            "Seguiment de ruta",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d("LocationService", "Servicio detenido (parada o tiempo excedido)")
    }

    private fun sendFinalizationBroadcast() {
        val intent = Intent("com.entrebicis.RUTA_FINALITZADA_AUTO")
        intent.setPackage(packageName)
        intent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
        Log.d("LOCATION_SERVICE", "📡 Broadcast enviat amb package: $packageName")
        sendBroadcast(intent)
    }
}
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
import android.os.IBinder
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import cat.copernic.p3grup1.entrebicis.core.network.RetrofitClient
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
    private var userEmail: String? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        // Inicializar repo y cliente
        routeRepo = RouteRepo(RetrofitClient.retrofit.create(RouteApi::class.java))
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtener email desde SharedPreferences
        val prefs = getSharedPreferences("usuari_prefs", Context.MODE_PRIVATE)
        userEmail = prefs.getString("email", null)

        createNotificationChannel()
        startForeground(1, buildNotification())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (location in result.locations) {
                    handleNewLocation(location)
                }
            }
        }

        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10_000L // cada 10s
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
        val email = userEmail ?: return

        val punt = PuntGpsDto(
            latitud = location.latitude,
            longitud = location.longitude,
            temps = System.currentTimeMillis()
        )

        CoroutineScope(Dispatchers.IO).launch {
            routeRepo.enviarPunt(email, punt)
        }
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
    }
}
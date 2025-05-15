package cat.copernic.p3grup1.entrebicis.home.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.p3grup1.entrebicis.core.location.LocationService
import cat.copernic.p3grup1.entrebicis.core.models.PuntGps
import cat.copernic.p3grup1.entrebicis.core.models.Usuari
import cat.copernic.p3grup1.entrebicis.core.network.RetrofitClient
import cat.copernic.p3grup1.entrebicis.core.utils.LogRutaUtils
import cat.copernic.p3grup1.entrebicis.core.utils.isInternetAvailable
import cat.copernic.p3grup1.entrebicis.route.data.repositories.RouteRepo
import cat.copernic.p3grup1.entrebicis.route.data.sources.remote.RouteApi
import cat.copernic.p3grup1.entrebicis.user_management.data.repositories.LoginRepo
import cat.copernic.p3grup1.entrebicis.user_management.data.sources.remote.UserApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application): AndroidViewModel(application) {

    @RequiresApi(Build.VERSION_CODES.O)
    private val repo = LoginRepo(RetrofitClient.getInstance(application.applicationContext).create(UserApi::class.java))
    @RequiresApi(Build.VERSION_CODES.O)
    private val repoRuta = RouteRepo(RetrofitClient.getInstance(application.applicationContext).create(RouteApi::class.java))
    private val prefs = application.getSharedPreferences("usuari_prefs", Context.MODE_PRIVATE)
    private val appContext = application.applicationContext

    private val _usuari = MutableStateFlow<Usuari?>(null)
    val usuari: StateFlow<Usuari?> = _usuari

    private val _navegarLogin = MutableStateFlow(false)
    val navegarLogin: StateFlow<Boolean> = _navegarLogin

    private val _rutaActiva = MutableStateFlow(false)
    val rutaActiva: StateFlow<Boolean> = _rutaActiva

    private val _tempsRuta = MutableStateFlow(0L) // en milisegons
    val tempsRuta: StateFlow<Long> = _tempsRuta

    private val _mostrarDetallRuta = MutableStateFlow(false)
    val mostrarDetallRuta: StateFlow<Boolean> = _mostrarDetallRuta

    private var cronometreHandler: Handler? = null
    private var cronometreRunnable: Runnable? = null
    private var iniciRutaTimestamp: Long = 0L

    fun logout() {
        prefs.edit().clear().apply()
        RetrofitClient.reset()
        val token = prefs.getString("token", null)
        Log.d("LOGOUT_CHECK", "Token después del logout: $token") // null = sesión cerrada
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun carregarUsuari() {
        val token = prefs.getString("token", null) ?: return
        Log.d("TOKEN_DEBUG", "Token: $token")
        val rutaActivaPref = prefs.getBoolean("ruta_activa", false)
        if(rutaActivaPref){
            Log.d("RUTA_RECUPERADA", "Detectada ruta activa. Finalitzant...")
            viewModelScope.launch {
                repoRuta.finalitzarRuta().onSuccess {
                    prefs.edit().remove("ruta_activa").apply()
                    Log.d("RUTA_RECUPERADA", "Ruta finalitzada automàticament al reiniciar app")
                }.onFailure {
                    Log.e("RUTA_RECUPERADA", "Error finalitzant ruta automàtica: ${it.message}")
                }
            }
        }
        viewModelScope.launch {
            if (!isInternetAvailable(appContext)) {
                Log.e("NETWORK", "No hi ha connexió a internet")
                return@launch
            }
            repo.getUsuari(token).fold(
                onSuccess = {
                    Log.d("HOME_VM", "Usuari rebut: ${it.nom} - ${it.saldo}")
                    _usuari.value = it },
                onFailure = { error ->
                    Log.e("HOME", "Error: ${error.message}")
                    val msg = error.message ?: ""
                    if(msg.contains("401")  || msg.contains("Usuari no trobat")){
                        logout() //eliminem token
                        _usuari.value = null
                        _navegarLogin.value = true
                    }
                }
            )
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun iniciarRuta() {
        if (_rutaActiva.value) return
        _rutaActiva.value = true
        prefs.edit().putBoolean("ruta_activa", true).apply()
        iniciRutaTimestamp = System.currentTimeMillis()
        startCronometre()
        iniciarServeiRuta()
        iniciarRutaBackend()
    }

    private fun iniciarServeiRuta(){
        val intent = Intent(appContext, LocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appContext.startForegroundService(intent)
        } else {
            appContext.startService(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun iniciarRutaBackend(){
        val timestamp = System.currentTimeMillis()
        Log.d("RUTA", "Ruta iniciada manualment a $timestamp")
        LogRutaUtils.appendLog(appContext, "Ruta iniciada manualment a $timestamp")

        val token = prefs.getString("token", null)
        if (token != null){
            viewModelScope.launch {
                if (!isInternetAvailable(appContext)) {
                    Log.e("RUTA", "❌ No hi ha connexió per iniciar la ruta al backend")
                    return@launch
                }
                repoRuta.iniciarRuta(token).onSuccess {
                    Log.d("RUTA", "✅ Ruta creada al backend: ${it.id}")
                }.onFailure {
                    Log.e("RUTA", "❌ Error creant ruta al backend: ${it.message}")
                }
            }
        }else {
            Log.e("RUTA", "❌ No s'ha pogut obtenir el token per crear la ruta")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun finalitzarRuta() {
        if (!_rutaActiva.value) return
        _rutaActiva.value = false
        prefs.edit().remove("ruta_activa").apply()
        stopCronometre()
        val intent = Intent(appContext, LocationService::class.java)
        appContext.stopService(intent)

        val timestamp = System.currentTimeMillis()
        Log.d("RUTA", "Ruta finalitzada amb durada de ${_tempsRuta.value} ms")
        LogRutaUtils.appendLog(appContext, "Ruta finalitzada manualment amb durada de ${_tempsRuta.value} ms a $timestamp")

        // Notificamos al backend que la ruta ha terminado
        viewModelScope.launch {
            if (!isInternetAvailable(appContext)) {
                Log.e("RUTA", "❌ No hi ha connexió per finalitzar la ruta al backend")
                return@launch
            }
            repoRuta.finalitzarRuta().onSuccess {
                _mostrarDetallRuta.value = true
                Log.d("RUTA", "Ruta finalitzada correctament al backend")
            }.onFailure {
                Log.e("RUTA", "Error finalitzant ruta: ${it.message}")
            }
        }
    }

    private fun startCronometre() {
        cronometreHandler = Handler(Looper.getMainLooper())
        cronometreRunnable = object : Runnable {
            override fun run() {
                val elapsed = System.currentTimeMillis() - iniciRutaTimestamp
                _tempsRuta.value = elapsed
                cronometreHandler?.postDelayed(this, 1000)
            }
        }
        cronometreHandler?.post(cronometreRunnable!!)
    }

    private fun stopCronometre() {
        cronometreHandler?.removeCallbacks(cronometreRunnable!!)
        cronometreHandler = null
        cronometreRunnable = null
        Log.d("CRONO", "Cronòmetre aturat")
    }

    fun resetNavegacio() {
        _mostrarDetallRuta.value = false
    }

}
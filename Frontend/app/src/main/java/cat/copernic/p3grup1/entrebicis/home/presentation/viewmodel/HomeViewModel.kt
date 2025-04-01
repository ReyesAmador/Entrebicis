package cat.copernic.p3grup1.entrebicis.home.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.p3grup1.entrebicis.core.models.PuntGps
import cat.copernic.p3grup1.entrebicis.core.models.Usuari
import cat.copernic.p3grup1.entrebicis.core.network.RetrofitClient
import cat.copernic.p3grup1.entrebicis.user_management.data.repositories.LoginRepo
import cat.copernic.p3grup1.entrebicis.user_management.data.sources.remote.UserApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application): AndroidViewModel(application) {

    @RequiresApi(Build.VERSION_CODES.O)
    private val repo = LoginRepo(RetrofitClient.retrofit.create(UserApi::class.java))
    private val prefs = application.getSharedPreferences("usuari_prefs", Context.MODE_PRIVATE)

    private val _usuari = MutableStateFlow<Usuari?>(null)
    val usuari: StateFlow<Usuari?> = _usuari

    private val _rutaActiva = MutableStateFlow(false)
    val rutaActiva: StateFlow<Boolean> = _rutaActiva

    private val _tempsRuta = MutableStateFlow(0L) // en milisegons
    val tempsRuta: StateFlow<Long> = _tempsRuta

    private var cronometreHandler: Handler? = null
    private var cronometreRunnable: Runnable? = null
    private var iniciRutaTimestamp: Long = 0L

    fun logout() {
        prefs.edit().clear().apply()
        val token = prefs.getString("token", null)
        Log.d("LOGOUT_CHECK", "Token después del logout: $token") // null = sesión cerrada
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun carregarUsuari() {
        val token = prefs.getString("token", null) ?: return
        Log.d("TOKEN_DEBUG", "Token: $token")
        viewModelScope.launch {
            repo.getUsuari(token).fold(
                onSuccess = {
                    Log.d("HOME_VM", "Usuari rebut: ${it.nom} - ${it.saldo}")
                    _usuari.value = it },
                onFailure = { Log.e("HOME", "Error: ${it.message}") }
            )
        }
    }
    fun iniciarRuta() {
        if (_rutaActiva.value) return
        _rutaActiva.value = true
        iniciRutaTimestamp = System.currentTimeMillis()
        startCronometre()
        // TODO: iniciar servei GPS i començar a guardar punts
    }

    fun finalitzarRuta() {
        if (!_rutaActiva.value) return
        _rutaActiva.value = false
        stopCronometre()
        // TODO: aturar servei GPS i desar punts
        Log.d("RUTA", "Ruta finalitzada amb durada de ${_tempsRuta.value} ms")
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
    }

}
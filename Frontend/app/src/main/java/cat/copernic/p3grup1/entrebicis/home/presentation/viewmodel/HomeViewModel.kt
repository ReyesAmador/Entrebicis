package cat.copernic.p3grup1.entrebicis.home.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
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

    val rutaActiva = MutableStateFlow(false)
    val tempsRuta = MutableStateFlow(0L)
    val puntsGPS = mutableListOf<PuntGps>()

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


}
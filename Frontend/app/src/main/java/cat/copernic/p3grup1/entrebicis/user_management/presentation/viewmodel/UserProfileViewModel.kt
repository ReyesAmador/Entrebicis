package cat.copernic.p3grup1.entrebicis.user_management.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.p3grup1.entrebicis.core.models.Usuari
import cat.copernic.p3grup1.entrebicis.core.network.RetrofitClient
import cat.copernic.p3grup1.entrebicis.user_management.data.repositories.LoginRepo
import cat.copernic.p3grup1.entrebicis.user_management.data.sources.remote.CanviContrasenyaRequest
import cat.copernic.p3grup1.entrebicis.user_management.data.sources.remote.UserApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class UserProfileViewModel(application: Application): AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("usuari_prefs", Context.MODE_PRIVATE)

    private val repo by lazy {
        LoginRepo(RetrofitClient.getInstance(application).create(UserApi::class.java))
    }

    private val _usuari = MutableStateFlow<Usuari?>(null)
    val usuari: StateFlow<Usuari?> = _usuari

    private val _actualitzacioExitosa = MutableStateFlow<Boolean?>(null)
    val actualitzacioExitosa: StateFlow<Boolean?> = _actualitzacioExitosa

    private val _errorActualitzacio = MutableStateFlow<String?>(null)
    val errorActualitzacio: StateFlow<String?> = _errorActualitzacio

    private val _imatgeBase64 = MutableStateFlow<String?>(null)
    val imatgeBase64: StateFlow<String?> = _imatgeBase64

    private val _missatgeContrasenya = MutableStateFlow<String?>(null)
    val missatgeContrasenya: StateFlow<String?> = _missatgeContrasenya

    fun carregarUsuari() {
        val token = prefs.getString("token", null) ?: return
        Log.d("TOKEN_DEBUG", "Token: $token")
        viewModelScope.launch {
            repo.getUsuari(token).fold(
                onSuccess = {
                    Log.d("PROFILE_DEBUG", "Imatge Base64: ${it.imatge?.take(20)}")
                    _usuari.value = it },
                onFailure = { error ->
                    Log.e("HOME", "Error: ${error.message}")
                    val msg = error.message ?: ""
                    if(msg.contains("401")  || msg.contains("Usuari no trobat")){
                        _usuari.value = null
                    }
                }
            )
        }
    }

    fun actualitzarUsuari(usuari: Usuari){
        val token = prefs.getString("token", null) ?: return
        viewModelScope.launch {
            repo.actualitzarUsuari(token, usuari).fold(
                onSuccess = {
                    _actualitzacioExitosa.value = true
                    carregarUsuari()
                },
                onFailure = {
                    _errorActualitzacio.value = it.message
                    _actualitzacioExitosa.value = false

                }
            )
        }
    }

    fun canviContrasenya(actual: String, nova: String, repetir: String) {
        val token = prefs.getString("token", null) ?: return

        if (nova != repetir) {
            _missatgeContrasenya.value = "❌ Les contrasenyes noves no coincideixen"
            return
        }

        val pattern = "^(?=\\S+$)(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{4,}$"
        if (!nova.matches(pattern.toRegex())) {
            _missatgeContrasenya.value = "❌ Contrasenya invàlida: mínim 4 caràcters, 1 minúscula, 1 majúscula i 1 símbol"
            return
        }

        viewModelScope.launch {
            val result = repo.canviContrasenya(token, CanviContrasenyaRequest(actual, nova, repetir))
            result.fold(
                onSuccess = {
                    _missatgeContrasenya.value = "✅ Contrasenya canviada correctament"
                },
                onFailure = {
                    _missatgeContrasenya.value = "❌ ${it.message}"
                }
            )
        }
    }

    fun resetActualitzacioFlags() {
        _actualitzacioExitosa.value = null
        _errorActualitzacio.value = null
    }

    fun resetMissatgeContrasenya() {
        _missatgeContrasenya.value = null
    }

    fun setImatgeBase64(base64: String) {
        _imatgeBase64.value = base64
    }

}
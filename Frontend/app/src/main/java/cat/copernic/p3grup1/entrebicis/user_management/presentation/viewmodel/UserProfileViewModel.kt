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
import cat.copernic.p3grup1.entrebicis.core.utils.isInternetAvailable
import cat.copernic.p3grup1.entrebicis.user_management.data.repositories.LoginRepo
import cat.copernic.p3grup1.entrebicis.user_management.data.sources.remote.CanviContrasenyaRequest
import cat.copernic.p3grup1.entrebicis.user_management.data.sources.remote.UserApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel per la gestió del perfil d'usuari:
 * - Consulta de dades
 * - Actualització de perfil
 * - Canvi de contrasenya
 * - Upload de la imatge de perfil
 */
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

    private val _uploadExitosa = MutableStateFlow(false)
    val uploadExitosa: StateFlow<Boolean> = _uploadExitosa

    private val _missatgeContrasenya = MutableStateFlow<String?>(null)
    val missatgeContrasenya: StateFlow<String?> = _missatgeContrasenya

    private val _errorConnexio = MutableStateFlow<String?>(null)
    val errorConnexio: StateFlow<String?> = _errorConnexio

    /** Carrega les dades de l'usuari loguejat. */
    fun carregarUsuari() {
        val token = getToken() ?: return
        Log.d("TOKEN_DEBUG", "Token: $token")
        viewModelScope.launch {
            if (!isInternetAvailable(getApplication())) {
                _errorConnexio.value = "⚠️ No hi ha connexió a internet"
                return@launch
            }
            val start = System.currentTimeMillis()
            repo.getUsuari(token).fold(
                onSuccess = {
                    Log.d("PROFILE_DEBUG", "Imatge Base64: ${it.imatge?.take(20)}")
                    Log.d("PROFILE", "Temps total: ${System.currentTimeMillis() - start} ms")
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

    /** Envia una petició per actualitzar les dades de l’usuari. */
    fun actualitzarUsuari(usuari: Usuari){
        val token = getToken() ?: return
        viewModelScope.launch {
            if (!isInternetAvailable(getApplication())) {
                _errorConnexio.value = "⚠️ No hi ha connexió a internet"
                return@launch
            }
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

    /**
     * Valida i envia una petició per canviar la contrasenya.
     * Comprova si coincideixen i compleixen la política.
     */
    fun canviContrasenya(actual: String, nova: String, repetir: String) {
        val token = getToken() ?: return

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
            if (!isInternetAvailable(getApplication())) {
                _errorConnexio.value = "⚠️ No hi ha connexió a internet"
                return@launch
            }
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

    /** Sube la imatge de perfil al backend. */
    suspend fun uploadImatgeUsuari(imageBytes: ByteArray): Boolean {
        val token = getToken() ?: return false
        if (!isInternetAvailable(getApplication())) {
            _errorConnexio.value = "⚠️ No hi ha connexió a internet"
            return false
        }
            return repo.uploadUserImage(token, imageBytes).fold(
                onSuccess = {
                    Log.d("UPLOAD_IMAGE", "✅ Imatge pujada correctament")
                    _uploadExitosa.value = true
                    true
                },
                onFailure = {
                    Log.e("UPLOAD_IMAGE", "❌ Error pujant imatge: ${it.message}")
                    false
                }
            )
    }

    fun resetActualitzacioFlags() {
        _actualitzacioExitosa.value = null
        _errorActualitzacio.value = null
    }

    fun resetMissatgeContrasenya() {
        _missatgeContrasenya.value = null
    }

    fun getToken(): String? {
        return prefs.getString("token", null)
    }

    fun resetUploadExitosa() {
        _uploadExitosa.value = false
    }

    fun clearErrorConnexio() {
        _errorConnexio.value = null
    }

}
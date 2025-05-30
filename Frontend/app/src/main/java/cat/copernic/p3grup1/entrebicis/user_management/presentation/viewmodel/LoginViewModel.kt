package cat.copernic.p3grup1.entrebicis.user_management.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.p3grup1.entrebicis.core.utils.isInternetAvailable
import cat.copernic.p3grup1.entrebicis.user_management.data.repositories.LoginRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel encarregat de gestionar el procés d'inici de sessió.
 * Valida credencials amb el backend i emmagatzema el token en SharedPreferences.
 *
 * @property repo Repositori amb les crides d'autenticació.
 */
class LoginViewModel(
    application: Application,
    private val repo: LoginRepo
) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("usuari_prefs", Context.MODE_PRIVATE)

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess : StateFlow<Boolean> = _loginSuccess

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /**
     * Intenta iniciar sessió amb les credencials donades.
     * Guarda el token si la resposta és satisfactòria.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun login(email: String, paraula: String){
        viewModelScope.launch {
            if (!isInternetAvailable(getApplication())) {
                _errorMessage.value = "No hi ha connexió a internet"
                _loginSuccess.value = false
                return@launch
            }
            val result = repo.login(email,paraula)
            result.fold(
                onSuccess = { response ->
                    //gardar token en sharedpreferences
                    prefs.edit().putString("token", response.token).apply()
                    _loginSuccess.value = true
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                    _loginSuccess.value = false
                }
            )
        }
    }

    /** Obté el token actual des de SharedPreferences. */
    fun getToken(): String? {
        return prefs.getString("token", null)
    }

    /** Neteja el missatge d'error. */
    fun clearError() {
        _errorMessage.value = null
    }

    fun setError(message: String) {
        _errorMessage.value = message
    }
}
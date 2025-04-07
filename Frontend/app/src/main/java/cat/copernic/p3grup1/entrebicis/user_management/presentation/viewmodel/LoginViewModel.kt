package cat.copernic.p3grup1.entrebicis.user_management.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.p3grup1.entrebicis.user_management.data.repositories.LoginRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    application: Application,
    private val repo: LoginRepo
) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("usuari_prefs", Context.MODE_PRIVATE)

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess : StateFlow<Boolean> = _loginSuccess

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    @RequiresApi(Build.VERSION_CODES.O)
    fun login(email: String, paraula: String){
        viewModelScope.launch {
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

    fun getToken(): String? {
        return prefs.getString("token", null)
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun setError(message: String) {
        _errorMessage.value = message
    }
}
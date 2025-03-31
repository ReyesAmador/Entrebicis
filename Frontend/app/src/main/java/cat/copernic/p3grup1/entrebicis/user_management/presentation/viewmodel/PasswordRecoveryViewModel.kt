package cat.copernic.p3grup1.entrebicis.user_management.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.p3grup1.entrebicis.user_management.data.repositories.LoginRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PasswordRecoveryViewModel(
    application: Application,
    private val repo: LoginRepo
) : AndroidViewModel(application) {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _code = MutableStateFlow("")
    val code: StateFlow<String> = _code

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    fun updateEmail(value: String) {
        _email.value = value
    }

    fun updateCode(value: String) {
        _code.value = value
    }

    fun updateNewPassword(value: String) {
        _newPassword.value = value
    }

    fun forgotPassword() {
        viewModelScope.launch {
            val result = repo.forgotPass(_email.value)
            result.fold(
                onSuccess = {
                    _success.value = true
                },
                onFailure = {
                    _errorMessage.value = it.message
                    _success.value = false
                }
            )
        }
    }

    fun validateCode() {
        viewModelScope.launch {
            val result = repo.validateCode(_email.value, _code.value)
            result.fold(
                onSuccess = {
                    _success.value = true
                },
                onFailure = {
                    _errorMessage.value = it.message
                    _success.value = false
                }
            )
        }
    }

    fun resetPassword() {
        viewModelScope.launch {
            val result = repo.resetPassword(_email.value, _code.value, _newPassword.value)
            result.fold(
                onSuccess = {
                    _success.value = true
                },
                onFailure = {
                    _errorMessage.value = it.message
                    _success.value = false
                }
            )
        }
    }

    fun clearState() {
        _errorMessage.value = null
        _success.value = false
    }
}
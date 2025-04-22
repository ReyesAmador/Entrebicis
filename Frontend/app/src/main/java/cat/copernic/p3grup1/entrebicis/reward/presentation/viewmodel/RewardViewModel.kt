package cat.copernic.p3grup1.entrebicis.reward.presentation.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.p3grup1.entrebicis.core.models.Recompensa
import cat.copernic.p3grup1.entrebicis.reward.data.repositories.RewardRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RewardViewModel(
    application: Application,
    private val rewardRepo: RewardRepo
): AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("usuari_prefs", Context.MODE_PRIVATE)

    private val _recompenses = MutableStateFlow<List<Recompensa>>(emptyList())
    val recompenses: StateFlow<List<Recompensa>> = _recompenses

    private val _reservaSuccess = MutableStateFlow<Boolean?>(null)
    val reservaSuccess: StateFlow<Boolean?> = _reservaSuccess

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun carregarRecompenses(){
        val token = prefs.getString("token", null) ?: return
        viewModelScope.launch {
            rewardRepo.getRecompenses(token).fold(
                onSuccess = {_recompenses.value = it},
                onFailure = {_error.value = it.message}
            )
        }
    }

    fun reservarRecompensa(id: Long) {
        val token = prefs.getString("token", null) ?: return
        viewModelScope.launch {
            rewardRepo.reservarRecompensa(id, token).fold(
                onSuccess = {
                    _reservaSuccess.value = true
                },
                onFailure = {
                    _reservaSuccess.value = false
                    _error.value = it.message
                }
            )
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearReservaStatus() {
        _reservaSuccess.value = null
        _error.value = null
    }
}
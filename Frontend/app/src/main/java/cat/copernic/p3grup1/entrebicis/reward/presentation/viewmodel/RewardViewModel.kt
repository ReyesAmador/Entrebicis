package cat.copernic.p3grup1.entrebicis.reward.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.p3grup1.entrebicis.core.models.Recompensa
import cat.copernic.p3grup1.entrebicis.core.models.RecompensaDetall
import cat.copernic.p3grup1.entrebicis.core.utils.isInternetAvailable
import cat.copernic.p3grup1.entrebicis.reward.data.repositories.RewardRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel encarregat de gestionar la lògica de recompenses:
 * - Llistat de recompenses disponibles
 * - Visualització del detall
 * - Reserves i recollida
 * - Control d'errors i estat de càrrega
 *
 * @property rewardRepo Repositori amb les crides a l'API de recompenses
 */
class RewardViewModel(
    application: Application,
    private val rewardRepo: RewardRepo
): AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("usuari_prefs", Context.MODE_PRIVATE)

    private val _recompenses = MutableStateFlow<List<Recompensa>>(emptyList())
    val recompenses: StateFlow<List<Recompensa>> = _recompenses

    private val _recompensa = MutableStateFlow<RecompensaDetall?>(null)
    val recompensa: StateFlow<RecompensaDetall?> = _recompensa

    private val _recompensaEntregada = MutableStateFlow(false)
    val recompensaEntregada: StateFlow<Boolean> = _recompensaEntregada

    private val _reservaSuccess = MutableStateFlow<Boolean?>(null)
    val reservaSuccess: StateFlow<Boolean?> = _reservaSuccess

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _errorConnexio = MutableStateFlow<String?>(null)
    val errorConnexio: StateFlow<String?> = _errorConnexio

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    /**
     * Carrega la llista de recompenses disponibles per l’usuari actual.
     * Comprova la connexió abans d'enviar la petició.
     */
    fun carregarRecompenses(){
        val token = prefs.getString("token", null) ?: return
        viewModelScope.launch {
            _isRefreshing.value = true
            if (!isInternetAvailable(getApplication())) {
                _errorConnexio.value = "⚠️ No hi ha connexió a internet"
                _isRefreshing.value = false
                return@launch
            }

            rewardRepo.getRecompenses(token).fold(
                onSuccess = {_recompenses.value = it},
                onFailure = {_error.value = it.message}
            )
            _isRefreshing.value = false
        }
    }

    /**
     * Envia una petició per reservar una recompensa específica.
     *
     * @param id ID de la recompensa a reservar.
     */
    fun reservarRecompensa(id: Long) {
        val token = prefs.getString("token", null) ?: return
        viewModelScope.launch {
            if (!isInternetAvailable(getApplication())) {
                _errorConnexio.value = "⚠️ No hi ha connexió a internet"
                return@launch
            }
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

    /**
     * Carrega el detall d’una recompensa concreta per mostrar informació completa.
     *
     * @param id ID de la recompensa.
     */
    fun carregarRecompensa(id: Long){
        val token = prefs.getString("token", null) ?: return
        viewModelScope.launch {
            _isRefreshing.value = true
            if (!isInternetAvailable(getApplication())) {
                _errorConnexio.value = "⚠️ No hi ha connexió a internet"
                _isRefreshing.value = false
                return@launch
            }
            rewardRepo.getRecompensa(token,id).fold(
                onSuccess = {
                    _recompensa.value = it
                },
                onFailure = {
                    Log.e("REWARD_DETAIL", "Error carregant recompensa: ${it.message}")
                }
            )
            _isRefreshing.value = false
        }
    }

    /**
     * Marca una recompensa com a recollida. Només es pot fer si hi ha connexió.
     *
     * @param id ID de la recompensa a recollir.
     */
    fun recollirRecompensa(id: Long){
        val token = prefs.getString("token", null) ?: return
        viewModelScope.launch {
            if (!isInternetAvailable(getApplication())) {
                _errorConnexio.value = "⚠️ No hi ha connexió a internet"
                return@launch
            }
            rewardRepo.recollirRecompensa(token, id).onSuccess {
                _recompensaEntregada.value = true
            }.onFailure {
                Log.e("REWARD", "Error recollint recompensa: ${it.message}")
            }
        }
    }

    /**
     * Reinicia l’estat que indica si una recompensa ha estat entregada.
     */
    fun resetEntrega() {
        _recompensaEntregada.value = false
    }

    fun clearError() {
        _error.value = null
    }

    /**
     * Neteja els errors de connexió detectats.
     */
    fun clearErrorConnexio() {
        _errorConnexio.value = null
    }

    /**
     * Neteja l’estat de reserva i els missatges d’error relacionats.
     */
    fun clearReservaStatus() {
        _reservaSuccess.value = null
        _error.value = null
    }
}
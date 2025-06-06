package cat.copernic.p3grup1.entrebicis.route.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.p3grup1.entrebicis.core.network.RetrofitClient
import cat.copernic.p3grup1.entrebicis.core.utils.isInternetAvailable
import cat.copernic.p3grup1.entrebicis.route.data.repositories.RouteRepo
import cat.copernic.p3grup1.entrebicis.route.data.sources.remote.PuntGpsDto
import cat.copernic.p3grup1.entrebicis.route.data.sources.remote.RouteApi
import cat.copernic.p3grup1.entrebicis.route.data.sources.remote.RutaAmbPuntsDto
import cat.copernic.p3grup1.entrebicis.route.data.sources.remote.RutaSensePuntsDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel encarregat de gestionar les dades relacionades amb les rutes:
 * - Carrega la ruta actual amb punts GPS.
 * - Carrega el llistat de rutes finalitzades.
 * - Controla els estats de càrrega i errors de connexió.
 *
 * @property repo Repositori per accedir a l’API de rutes.
 */
@RequiresApi(Build.VERSION_CODES.O)
class RutaViewModel(
    application: Application,
    private val repo: RouteRepo) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("usuari_prefs", Context.MODE_PRIVATE)

    private val _detallRuta = MutableStateFlow<RutaAmbPuntsDto?>(null)
    val detallRuta: StateFlow<RutaAmbPuntsDto?> = _detallRuta
    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading
    private val _rutesFinalitzades = MutableStateFlow<List<RutaSensePuntsDto>>(emptyList())
    val rutesFinalitzades: StateFlow<List<RutaSensePuntsDto>> = _rutesFinalitzades
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _errorConnexio = MutableStateFlow<String?>(null)
    val errorConnexio: StateFlow<String?> = _errorConnexio

    /**
     * Carrega la ruta amb punts GPS.
     * Si es proporciona un ID, carrega una ruta específica; si no, carrega l’última ruta finalitzada.
     * Comprova primer si hi ha connexió a Internet.
     *
     * @param id Identificador de la ruta concreta, opcional.
     */
    fun carregarRuta(id: Long? = null){
        viewModelScope.launch {
            _loading.value = true
            if (!isInternetAvailable(getApplication())) {
                _errorConnexio.value = "⚠️ No hi ha connexió a internet"
                _loading.value = false
                return@launch
            }
            val resultat = if (id != null){
                repo.getDetallRutaEspecifica(id)
            }else{
                repo.getRutaAmbPunts() //última ruta finalizada
            }
            resultat.onSuccess {
                _detallRuta.value = it
                delay(1500)
                _loading.value = false
            }.onFailure {
                Log.e("DETALL", "Error carregant ruta: ${it.message}")
                _loading.value = false
            }
        }
    }

    /**
     * Carrega totes les rutes finalitzades per l’usuari.
     * Només s’executa si es disposa del token i connexió.
     */
    fun carregarRutesFinalitzades(){
        val token = prefs.getString("token", null) ?: return
        viewModelScope.launch {
            _isRefreshing.value = true
            if (!isInternetAvailable(getApplication())) {
                _errorConnexio.value = "⚠️ No hi ha connexió a internet"
                _loading.value = false
                return@launch
            }

            repo.getRutesFinalitzades(token).onSuccess {
                _rutesFinalitzades.value = it
            }.onFailure {
                Log.e("RUTES", "Error carregant rutes: ${it.message}")
            }
            _isRefreshing.value = false
        }
    }

    /**
     * Neteja el missatge d’error de connexió, si n’hi havia.
     */
    fun clearErrorConnexio() {
        _errorConnexio.value = null
    }
}
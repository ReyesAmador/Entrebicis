package cat.copernic.p3grup1.entrebicis.route.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cat.copernic.p3grup1.entrebicis.core.network.RetrofitClient
import cat.copernic.p3grup1.entrebicis.route.data.repositories.RouteRepo
import cat.copernic.p3grup1.entrebicis.route.data.sources.remote.PuntGpsDto
import cat.copernic.p3grup1.entrebicis.route.data.sources.remote.RouteApi
import cat.copernic.p3grup1.entrebicis.route.data.sources.remote.RutaAmbPuntsDto
import cat.copernic.p3grup1.entrebicis.route.data.sources.remote.RutaSensePuntsDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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


    fun carregarRuta(id: Long? = null){
        viewModelScope.launch {
            _loading.value = true
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

    fun carregarRutesFinalitzades(){
        val token = prefs.getString("token", null) ?: return
        viewModelScope.launch {
            repo.getRutesFinalitzades(token).onSuccess {
                _rutesFinalitzades.value = it
            }.onFailure {
                Log.e("RUTES", "Error carregant rutes: ${it.message}")
            }
        }
    }
}
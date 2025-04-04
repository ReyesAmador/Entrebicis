package cat.copernic.p3grup1.entrebicis.route.presentation.viewmodel

import android.app.Application
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class RutaViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = RouteRepo(
        RetrofitClient.getInstance(application.applicationContext).create(RouteApi::class.java)
    )

    private val _detallRuta = MutableStateFlow<RutaAmbPuntsDto?>(null)
    val detallRuta: StateFlow<RutaAmbPuntsDto?> = _detallRuta


    fun carregarRuta(){
        viewModelScope.launch {
            repo.getRutaAmbPunts().onSuccess {
                _detallRuta.value = it
            }.onFailure {
                Log.e("DETALL", "Error carregant ruta: ${it.message}")
            }
        }
    }


}
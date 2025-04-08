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

    fun carregarUsuari() {
        val token = prefs.getString("token", null) ?: return
        Log.d("TOKEN_DEBUG", "Token: $token")
        viewModelScope.launch {
            repo.getUsuari(token).fold(
                onSuccess = {
                    Log.d("HOME_VM", "Usuari rebut: ${it.nom} - ${it.saldo}")
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
}
package cat.copernic.p3grup1.entrebicis.home.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel

class HomeViewModel(application: Application): AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("usuari_prefs", Context.MODE_PRIVATE)

    val username = "Pablo"
    val punts = 450

    fun logout() {
        prefs.edit().clear().apply()
        val token = prefs.getString("token", null)
        Log.d("LOGOUT_CHECK", "Token después del logout: $token") // null = sesión cerrada
    }
}
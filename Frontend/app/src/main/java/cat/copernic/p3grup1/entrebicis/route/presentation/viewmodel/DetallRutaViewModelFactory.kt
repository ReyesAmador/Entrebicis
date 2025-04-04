package cat.copernic.p3grup1.entrebicis.route.presentation.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cat.copernic.p3grup1.entrebicis.core.network.RetrofitClient
import cat.copernic.p3grup1.entrebicis.route.data.repositories.RouteRepo

@RequiresApi(Build.VERSION_CODES.O)
fun detallRutaViewModelFactory(application: Application) = viewModelFactory {

    initializer {
        val api = RetrofitClient.getInstance(application.applicationContext).create(cat.copernic.p3grup1.entrebicis.route.data.sources.remote.RouteApi::class.java)
        val repo = RouteRepo(api)
        RutaViewModel(application, repo)
    }
}
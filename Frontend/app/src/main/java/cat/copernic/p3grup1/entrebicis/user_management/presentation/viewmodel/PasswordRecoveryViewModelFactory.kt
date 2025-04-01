package cat.copernic.p3grup1.entrebicis.user_management.presentation.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cat.copernic.p3grup1.entrebicis.core.network.RetrofitClient
import cat.copernic.p3grup1.entrebicis.user_management.data.repositories.LoginRepo

@RequiresApi(Build.VERSION_CODES.O)
fun passwordRecoveryViewModelFactory(application: Application) = viewModelFactory {
    initializer {
        val api = RetrofitClient.getInstance(application.applicationContext).create(
            cat.copernic.p3grup1.entrebicis.user_management.data.sources.remote.UserApi::class.java
        )
        val repo = LoginRepo(api)
        PasswordRecoveryViewModel(application, repo)
    }
}
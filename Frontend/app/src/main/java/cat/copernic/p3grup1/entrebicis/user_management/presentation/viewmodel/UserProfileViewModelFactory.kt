package cat.copernic.p3grup1.entrebicis.user_management.presentation.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

@RequiresApi(Build.VERSION_CODES.O)
fun provideUserProfileViewModelFactory(application: Application) = viewModelFactory {
    initializer {
        UserProfileViewModel(application)
    }
}
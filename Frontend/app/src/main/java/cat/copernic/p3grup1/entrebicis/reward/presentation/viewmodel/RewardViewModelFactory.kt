package cat.copernic.p3grup1.entrebicis.reward.presentation.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cat.copernic.p3grup1.entrebicis.core.network.RetrofitClient
import cat.copernic.p3grup1.entrebicis.reward.data.repositories.RewardRepo

@RequiresApi(Build.VERSION_CODES.O)
fun provideRewardViewModelFactory(application: Application) = viewModelFactory {
    initializer {
        val api = RetrofitClient.getInstance(application.applicationContext)
            .create(cat.copernic.p3grup1.entrebicis.reward.data.sources.remote.RewardApi::class.java)
        val repo = RewardRepo(api)
        RewardViewModel(application, repo)
    }
}
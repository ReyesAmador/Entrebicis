package cat.copernic.p3grup1.entrebicis.splash.presentation

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.p3grup1.entrebicis.R
import cat.copernic.p3grup1.entrebicis.user_management.presentation.viewmodel.LoginViewModel
import cat.copernic.p3grup1.entrebicis.user_management.presentation.viewmodel.provideLoginViewModelFactory
import kotlinx.coroutines.delay

/**
 * Pantalla inicial de l'aplicació (SplashScreen).
 * Mostra el logo amb una animació de rotació mentre es comprova si hi ha un token de sessió guardat.
 * Segons el resultat, redirigeix a la pantalla de login o a la pantalla principal (Home).
 *
 * @param onNavigateToLogin Funció de navegació cap a la pantalla de login si no hi ha sessió iniciada.
 * @param onNavigateToHome Funció de navegació cap a la pantalla principal si la sessió està activa.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: LoginViewModel = viewModel(
        factory = provideLoginViewModelFactory(context.applicationContext as Application)
    )

    //animación
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    LaunchedEffect(Unit) {
        delay(2000)
        val token = viewModel.getToken()
        Log.d("TOKEN_CHECK", "Token actual: $token")
        if (token.isNullOrEmpty()) {
            Log.d("TOKEN_CHECK", "Token vacío. Navegando a Login.")
            onNavigateToLogin()
        } else {
            Log.d("TOKEN_CHECK", "Token válido. Navegando a Home.")
            onNavigateToHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_white),
            contentDescription = "Logo",
            modifier = Modifier
                .size(180.dp)
                .rotate(rotation)
        )
    }
}
package cat.copernic.p3grup1.entrebicis.home.presentation.screen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cat.copernic.p3grup1.entrebicis.R
import cat.copernic.p3grup1.entrebicis.core.utils.formatTime
import cat.copernic.p3grup1.entrebicis.home.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    homeViewModel: HomeViewModel = viewModel()) {

    val usuari by homeViewModel.usuari.collectAsState()
    val rutaActiva by homeViewModel.rutaActiva.collectAsState()
    val tempsRuta by homeViewModel.tempsRuta.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        homeViewModel.carregarUsuari()
    }

    DisposableEffect(Unit) {
        val filter = IntentFilter("com.entrebicis.RUTA_FINALITZADA_AUTO")
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                homeViewModel.finalitzarRuta()
                // Mostrar Snackbar en coroutine
                CoroutineScope(Dispatchers.Main).launch {
                    snackbarHostState.showSnackbar("Ruta finalitzada automàticament per aturada")
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(receiver, filter)
        }

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(48.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_black),
                contentDescription = "Logo Entrebicis"
            )


            Text("Benvingut, ${usuari?.nom ?: "..."}", style = MaterialTheme.typography.headlineLarge)

            Spacer(Modifier.height(16.dp))

            Text(
                "Tens ${usuari?.saldo ?: 0} punts",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            )

            Spacer(Modifier.height(100.dp))

            if (rutaActiva) {
                Text(
                    text = "Temps: ${formatTime(tempsRuta)}",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = {
                    if (rutaActiva) homeViewModel.finalitzarRuta()
                    else homeViewModel.iniciarRuta()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .height(38.dp)
                    .width(240.dp)
            ) {
                Text(
                    text = if (rutaActiva) "FINALITZAR RUTA" else "INICIAR RUTA",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(90.dp))

            Button(
                onClick = {
                    homeViewModel.logout()
                    onLogout()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .height(38.dp)
                    .width(240.dp)
            ) {
                Text("TANCAR SESSIÓ", style = MaterialTheme.typography.labelLarge, color = Color.White)
            }
        }
    }
}
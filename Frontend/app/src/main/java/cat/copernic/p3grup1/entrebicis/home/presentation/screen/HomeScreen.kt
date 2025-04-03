package cat.copernic.p3grup1.entrebicis.home.presentation.screen

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.p3grup1.entrebicis.R
import cat.copernic.p3grup1.entrebicis.core.utils.formatTime
import cat.copernic.p3grup1.entrebicis.home.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnspecifiedRegisterReceiverFlag")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {

    val usuari by homeViewModel.usuari.collectAsState()
    val navegarLogin by homeViewModel.navegarLogin.collectAsState()
    val rutaActiva by homeViewModel.rutaActiva.collectAsState()
    val tempsRuta by homeViewModel.tempsRuta.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity
    val snackbarHostState = remember { SnackbarHostState() }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                    permissions[Manifest.permission.FOREGROUND_SERVICE_LOCATION] == true
        } else {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        }

        if (granted) {
            homeViewModel.iniciarRuta()
        } else {
            Toast.makeText(context, "Cal acceptar els permisos de localització", Toast.LENGTH_LONG)
                .show()
        }
    }

    LaunchedEffect(navegarLogin) {
        if (navegarLogin) {
            onLogout() // redirige al login
        }
    }

    LaunchedEffect(Unit) {
        homeViewModel.carregarUsuari()
    }

    DisposableEffect(Unit) {
        val filter = IntentFilter("com.entrebicis.RUTA_FINALITZADA_AUTO")
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d("HOME_SCREEN", "📡 Broadcast rebut correctament!!")
                homeViewModel.finalitzarRuta()
                CoroutineScope(Dispatchers.Main).launch {
                    snackbarHostState.showSnackbar("Ruta finalitzada automàticament per aturada")
                }
            }
        }

        activity?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                it.registerReceiver(receiver, filter)
            }
            Log.d("HOME_SCREEN", "✅ Receiver registrat correctament")
        }

        onDispose {
            activity?.unregisterReceiver(receiver)
            Log.d("HOME_SCREEN", "🧹 Receiver eliminat")
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


            Text(
                "Benvingut, ${usuari?.nom ?: "..."}",
                style = MaterialTheme.typography.headlineLarge
            )

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
                    else {
                        if (ContextCompat.checkSelfPermission(
                                context, Manifest.permission.ACCESS_FINE_LOCATION
                            )
                            == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(
                                context, Manifest.permission.FOREGROUND_SERVICE_LOCATION
                            )
                            == PackageManager.PERMISSION_GRANTED
                        ) {

                            homeViewModel.iniciarRuta()

                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.FOREGROUND_SERVICE_LOCATION
                                    )
                                )
                            } else {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    )
                                )
                            }
                        }
                    }
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
                Text(
                    "TANCAR SESSIÓ",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
            }
        }
    }
}
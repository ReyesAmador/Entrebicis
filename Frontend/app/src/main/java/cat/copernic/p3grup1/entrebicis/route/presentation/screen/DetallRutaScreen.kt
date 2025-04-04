package cat.copernic.p3grup1.entrebicis.route.presentation.screen

import android.app.Application
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.p3grup1.entrebicis.route.presentation.components.InfoCard
import cat.copernic.p3grup1.entrebicis.route.presentation.viewmodel.RutaViewModel
import cat.copernic.p3grup1.entrebicis.route.presentation.viewmodel.detallRutaViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetallRutaScreen(
    onBack: () -> Unit,
    viewModel: RutaViewModel = viewModel(factory = detallRutaViewModelFactory(LocalContext.current.applicationContext as Application))
) {

    val detallRuta by viewModel.detallRuta.collectAsState()
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(Unit) {
        viewModel.carregarRuta()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detall Ruta", style = MaterialTheme.typography.headlineSmall) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Enrere")
                    }
                }
            )
        }
    ) { padding ->
        detallRuta?.let { ruta ->
            val punts = ruta.punts.map { LatLng(it.latitud, it.longitud) }

            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {

                // 🗺️ MAPA
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    cameraPositionState = cameraPositionState,
                    onMapLoaded = {
                        if (punts.isNotEmpty()) {
                            cameraPositionState.move(
                                CameraUpdateFactory.newLatLngZoom(
                                    punts.first(),
                                    15f
                                )
                            )
                        }
                    }
                ) {
                    if (punts.isNotEmpty()) {
                        Polyline(points = punts)

                        ruta.punts.forEach { punt ->
                            Marker(
                                state = rememberMarkerState(position = LatLng(punt.latitud, punt.longitud)),
                                title = "Punt GPS",
                                snippet = "Lat: ${punt.latitud}, Lon: ${punt.longitud}",
                                onClick = {
                                    Toast
                                        .makeText(
                                            context,
                                            "📍 Lat: ${punt.latitud}, Lon: ${punt.longitud}",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                    false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // 📦 ESTADÍSTIQUES
                InfoCard(
                    label = "Distància total",
                    value = String.format("%.2f km", ruta.distanciaTotal)
                )
                InfoCard(label = "Temps total", value = ruta.tempsTotal)
                InfoCard(
                    label = "Velocitat mitjana",
                    value = String.format("%.2f km/h", ruta.velocitatMitjana)
                )
                InfoCard(
                    label = "Velocitat màxima",
                    value = String.format("%.2f km/h", ruta.velocitatMaxima)
                )
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

}


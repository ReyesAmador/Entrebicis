package cat.copernic.p3grup1.entrebicis.route.presentation.screen

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.p3grup1.entrebicis.route.presentation.components.InfoCard
import cat.copernic.p3grup1.entrebicis.route.presentation.viewmodel.RutaViewModel
import cat.copernic.p3grup1.entrebicis.route.presentation.viewmodel.detallRutaViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetallRutaScreen(
    onBack: () -> Unit,
    viewModel: RutaViewModel = viewModel(factory = detallRutaViewModelFactory(LocalContext.current.applicationContext as Application))
) {

    val detallRuta by viewModel.detallRuta.collectAsState()
    val cameraPositionState = rememberCameraPositionState()
    val showPoints by remember {
        derivedStateOf { cameraPositionState.position.zoom >= 17f }
    }

    val mostrarMissatgeZoom = remember { mutableStateOf(false) }
    val haMostratMissatge = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.carregarRuta()
    }

    LaunchedEffect(cameraPositionState.position.zoom) {
        if (cameraPositionState.position.zoom < 17f) {
            mostrarMissatgeZoom.value = true

            // Esperamos hasta 5 segundos, pero salimos antes si el zoom cambia
            repeat(5) {
                kotlinx.coroutines.delay(1000)

                // Si el usuario se ha acercado, ocultamos el mensaje inmediatamente
                if (cameraPositionState.position.zoom >= 17f) {
                    mostrarMissatgeZoom.value = false
                    return@LaunchedEffect
                }
            }

            // Ocultar tras 5 segundos si el usuario no hizo zoom
            mostrarMissatgeZoom.value = false
        } else {
            // Si el zoom es suficiente, ocultamos el mensaje por si acaso
            mostrarMissatgeZoom.value = false
        }
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {

                    // 🗺️ MAPA
                    GoogleMap(
                        modifier = Modifier.matchParentSize(),
                        cameraPositionState = cameraPositionState,
                        onMapLoaded = {
                            if (punts.isNotEmpty()) {
                                val primer = punts.first()
                                cameraPositionState.move(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(primer.latitude, primer.longitude),
                                        15f
                                    )
                                )
                            }
                        }
                    ) {
                        if (punts.isNotEmpty()) {
                            Polyline(
                                points = punts,
                                color = Color.Blue,
                                width = 8f
                            )

                            if (showPoints) {
                                ruta.punts.forEach { punt ->
                                    Circle(
                                        center = LatLng(punt.latitud, punt.longitud),
                                        radius = 3.0,
                                        strokeColor = Color.White,
                                        strokeWidth = 1f,
                                        fillColor = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // 🧭 Mensaje si el zoom es bajo
                    androidx.compose.animation.AnimatedVisibility(
                        visible = mostrarMissatgeZoom.value,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 32.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.7f)
                        ) {
                            Text(
                                text = "📍 Acosta't per veure els punts",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

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


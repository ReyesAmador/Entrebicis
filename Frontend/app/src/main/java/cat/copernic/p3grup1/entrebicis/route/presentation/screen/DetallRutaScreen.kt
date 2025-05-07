package cat.copernic.p3grup1.entrebicis.route.presentation.screen

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
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
import cat.copernic.p3grup1.entrebicis.core.theme.Primary
import cat.copernic.p3grup1.entrebicis.route.data.sources.remote.PuntGpsDto
import cat.copernic.p3grup1.entrebicis.route.presentation.components.InfoCard
import cat.copernic.p3grup1.entrebicis.route.presentation.viewmodel.RutaViewModel
import cat.copernic.p3grup1.entrebicis.route.presentation.viewmodel.detallRutaViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetallRutaScreen(
    idRuta: Long? = null,
    onBack: () -> Unit,
    viewModel: RutaViewModel = viewModel(factory = detallRutaViewModelFactory(LocalContext.current.applicationContext as Application))
) {

    val detallRuta by viewModel.detallRuta.collectAsState()
    val cameraPositionState = rememberCameraPositionState()
    val mapLoaded = remember { mutableStateOf(false) }
    val showPoints by remember {
        derivedStateOf { cameraPositionState.position.zoom >= 17f }
    }
    val loading by viewModel.loading.collectAsState()
    val hasStartedLoading = remember { mutableStateOf(false) }

    val mostrarMissatgeZoom = remember { mutableStateOf(false) }
    val selectedPunt = remember { mutableStateOf<PuntGpsDto?>(null) }

    LaunchedEffect(Unit) {
        viewModel.carregarRuta()
        hasStartedLoading.value = true
    }

    LaunchedEffect(idRuta) {
        viewModel.carregarRuta(idRuta)
    }

    if (!hasStartedLoading.value || loading) {

        // 🔄 Mostrar CARGANDO antes de lanzar carga
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
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

    Scaffold { padding ->
        detallRuta?.let { ruta ->
            val punts = ruta.punts.map { LatLng(it.latitud, it.longitud) }

            Column(
                modifier = Modifier
                    .padding()
                    .fillMaxSize()
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(184.dp)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { onBack() },
                        modifier = Modifier.align(Alignment.TopStart).padding(start = 8.dp, top = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Enrere",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Aquesta és la teva ruta",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.dp)
                        .weight(1f)
                ) {

                    // 🗺️ MAPA
                    GoogleMap(
                        modifier = Modifier.matchParentSize(),
                        cameraPositionState = cameraPositionState,
                        onMapLoaded = {
                            mapLoaded.value = true
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
                                    val isSelected = selectedPunt.value == punt
                                    val latLng = LatLng(punt.latitud, punt.longitud)
                                    Circle(
                                        center = latLng,
                                        radius = 3.0,
                                        strokeColor = if (isSelected) Color.Blue else Primary,
                                        strokeWidth = 2f,
                                        fillColor = if (isSelected) Color(0xFF64B5F6) else Primary
                                    )

                                    //Marker invisible
                                    Marker(
                                        state = MarkerState(position = latLng),
                                        onClick = {
                                            selectedPunt.value = punt
                                            true //indica que gestionamos el evento
                                        },
                                        alpha = 0f //totalmente invisible
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
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF000000).copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = "📍 Acosta't per veure els punts",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // 🧾 Detall del punt seleccionat
                    selectedPunt.value?.let { punt ->
                        Surface(
                            modifier = Modifier
                                .wrapContentWidth()
                                .wrapContentHeight()
                                .align(Alignment.BottomCenter),
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            shadowElevation = 8.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "📍 Punt seleccionat",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Latitud: %.5f".format(punt.latitud),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "Longitud: %.5f".format(punt.longitud),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                androidx.compose.material3.TextButton(onClick = {
                                    selectedPunt.value = null
                                }) {
                                    Text("Tancar", color = Color.White, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(36.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoCard(
                            label = "Distància total", value = String.format("%.2f km", ruta.distanciaTotal)
                        )
                        InfoCard(
                            label = "Temps total", value = ruta.tempsTotal)
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoCard(
                            label = "Velocitat mitjana",value = String.format("%.2f km/h", ruta.velocitatMitjana))
                        InfoCard(
                            label = "Velocitat màxima",value = String.format("%.2f km/h", ruta.velocitatMaxima))
                    }
                }
            }
        }
    }
}


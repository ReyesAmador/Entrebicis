package cat.copernic.p3grup1.entrebicis.reward.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cat.copernic.p3grup1.entrebicis.R
import cat.copernic.p3grup1.entrebicis.core.components.RefreshIndicator
import cat.copernic.p3grup1.entrebicis.core.models.RecompensaDetall
import cat.copernic.p3grup1.entrebicis.core.theme.Primary
import cat.copernic.p3grup1.entrebicis.reward.presentation.components.InfoRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardDetailScreen(
    recompensa: RecompensaDetall,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onBack: () -> Unit,
    onReservar: (Long) -> Unit,
    onRecollir: (Long) -> Unit,
    entregat: Boolean,
    onTancarDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mostrarBoto = when (recompensa.estat) {
        "DISPONIBLE" -> true
        "ASSIGNADA" -> true
        else -> false
    }

    val textBoto = when (recompensa.estat) {
        "DISPONIBLE" -> "RESERVAR"
        "ASSIGNADA" -> "RECOLLIR"
        else -> ""
    }

    val scrollState = rememberScrollState()
    val showDialog = remember { mutableStateOf(false) }
    val pullState = rememberPullToRefreshState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Primary)
                .height(184.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = { onBack() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 8.dp, top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Botó per anar enrere",
                    tint = Color.White
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Image(
                    painter = painterResource(id = R.drawable.logo_white_small),
                    contentDescription = "Logo Entrebicis",
                    modifier = Modifier
                        .width(170.dp)
                        .height(82.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Recompensa",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            state = pullState,
            modifier = modifier.fillMaxSize(),
            indicator = {
                RefreshIndicator(
                    isRefreshing = isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        ) {

            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxSize()
                    .padding(top = 32.dp, bottom = 32.dp, start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Descripció
                Text(
                    text = recompensa.descripcio,
                    style = MaterialTheme.typography.headlineMedium,
                )

                Spacer(modifier = Modifier.height(28.dp))

                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(1.dp)
                        .shadow(elevation = 2.dp, shape = MaterialTheme.shapes.small)
                        .background(Color.Black)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Estat
                Text(
                    text = recompensa.estat,
                    style = MaterialTheme.typography.headlineSmall,
                )


                Spacer(modifier = Modifier.height(48.dp))

                InfoRow(
                    icon = Icons.Default.CalendarToday,
                    label = "Data creació:",
                    value = recompensa.dataCreacio
                )
                if (recompensa.nomUsuari.isNotBlank()) {
                    InfoRow(
                        icon = Icons.Default.Person,
                        label = "Usuari:",
                        value = recompensa.nomUsuari
                    )
                }
                InfoRow(
                    icon = Icons.Default.Store,
                    label = "Punt de recollida:",
                    value = recompensa.nomPunt
                )
                InfoRow(
                    icon = Icons.Default.LocationOn,
                    label = "Adreça: ",
                    value = recompensa.direccio
                )
                InfoRow(
                    icon = Icons.Default.BookmarkAdded,
                    label = "Data de reserva:",
                    value = recompensa.dataReserva
                )
                InfoRow(
                    icon = Icons.Default.AssignmentInd,
                    label = "Data assignació:",
                    value = recompensa.dataAssignacio
                )
                InfoRow(
                    icon = Icons.Default.CheckCircle,
                    label = "Data recollida:",
                    value = recompensa.dataRecollida
                )
                InfoRow(
                    icon = Icons.Default.Visibility,
                    label = "Observacions:",
                    value = recompensa.observacions
                )

                Spacer(modifier = Modifier.height(28.dp))

                if (showDialog.value) {
                    AlertDialog(
                        onDismissRequest = {
                            showDialog.value = false
                            onTancarDialog()
                        },
                        title = {
                            Text(
                                text = recompensa.descripcio,
                                style = MaterialTheme.typography.headlineLarge
                            )
                        },
                        text = {
                            if (!entregat) {
                                Text(
                                    text = recompensa.nomPunt,
                                    style = MaterialTheme.typography.headlineMedium
                                )
                            } else {
                                Text(
                                    "✅ ENTREGAT",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = Primary
                                )
                            }
                        },
                        confirmButton = {
                            if (!entregat) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Button(onClick = { onRecollir(recompensa.id) }) {
                                        Text("ENTREGAT")
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Button(onClick = {
                                        showDialog.value = false
                                        onTancarDialog()
                                        onBack()
                                    }) {
                                        Text("TANCAR")
                                    }
                                }
                            }
                        }
                    )
                }

                if (mostrarBoto) {
                    Button(
                        onClick = {
                            when (recompensa.estat) {
                                "DISPONIBLE" -> onReservar(recompensa.id)
                                "ASSIGNADA" -> showDialog.value = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        modifier = Modifier
                            .height(38.dp)
                            .fillMaxWidth(0.8f)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = textBoto,
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}
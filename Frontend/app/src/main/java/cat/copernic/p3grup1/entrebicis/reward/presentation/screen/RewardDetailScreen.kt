package cat.copernic.p3grup1.entrebicis.reward.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cat.copernic.p3grup1.entrebicis.core.models.RecompensaDetall
import cat.copernic.p3grup1.entrebicis.core.theme.Primary
import cat.copernic.p3grup1.entrebicis.reward.presentation.components.InfoRow

@Composable
fun RewardDetailScreen(
    recompensa : RecompensaDetall,
    onBack: () -> Unit,
    onReservar: (Long) -> Unit,
    modifier: Modifier = Modifier
){
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
            Text(
                text = "Recompensa",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Descripció
        Text(
            text = recompensa.descripcio,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Box(
            modifier = Modifier
                .padding(top = 28.dp)
                .width(140.dp)
                .height(1.dp)
                .align(Alignment.CenterHorizontally)
                .shadow(elevation = 2.dp, shape = MaterialTheme.shapes.small)
                .background(Color.Black)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Estat
        Text(
            text = recompensa.estat,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)) {

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
            InfoRow(icon = Icons.Default.LocationOn, label = "Adreça: ", value = recompensa.direccio)
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

            if (mostrarBoto) {
                Button(
                    onClick = {
                        if(recompensa.estat == "DISPONIBLE"){
                            onReservar(recompensa.id)
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
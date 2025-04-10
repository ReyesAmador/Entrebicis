package cat.copernic.p3grup1.entrebicis.route.presentation.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cat.copernic.p3grup1.entrebicis.route.data.sources.remote.RutaSensePuntsDto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RutaCard(ruta: RutaSensePuntsDto, modifier: Modifier = Modifier) {

    val dataFormatejada = try {
        val parsedDateTime = LocalDateTime.parse(ruta.inici)
        parsedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm"))
    } catch (e: Exception) {
        "Data invàlida"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = dataFormatejada,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )
            Text(
                text = if (ruta.validada) "Valida" else "No vàlida",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )
        }

        Spacer(Modifier.height(4.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "Vel. Max: ${ruta.velocitat_max.toInt()} km/h",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            Text(
                text = "Distància: ${"%.1f".format(ruta.km_total)} Km",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }

        Spacer(Modifier.height(4.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "Vel. Mitj: ${ruta.velocitat_mitjana.toInt()} km/h",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            Text(
                text = "Temps: ${ruta.temps_total}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}

package cat.copernic.p3grup1.entrebicis.reward.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cat.copernic.p3grup1.entrebicis.core.enums.EstatRecompensa
import cat.copernic.p3grup1.entrebicis.core.models.Recompensa
import cat.copernic.p3grup1.entrebicis.core.theme.Assigned
import cat.copernic.p3grup1.entrebicis.core.theme.Primary
import cat.copernic.p3grup1.entrebicis.core.theme.Secondary
import cat.copernic.p3grup1.entrebicis.core.theme.Tertiary

@Composable
fun RewardCard(
    recompensa: Recompensa,
    modifier: Modifier = Modifier
) {
    val colorText: Pair<Color, String> = when (recompensa.estat) {
        EstatRecompensa.DISPONIBLE -> Primary to "Reservar"
        EstatRecompensa.ASSIGNADA -> Assigned to (recompensa.usuari?.nom ?: "")
        EstatRecompensa.RESERVADA -> Secondary to (recompensa.usuari?.nom ?: "")
        EstatRecompensa.RECOLLIDA -> Tertiary to (recompensa.usuari?.nom ?: "")
        else -> Color.LightGray to ""
    }

    val(color, text) = colorText

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .background(Secondary, RoundedCornerShape(4.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = recompensa.descripcio ?: "",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                Text(
                    text = recompensa.puntRecollida ?: "",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            }

            Text(
                text = recompensa.direccio ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )

            Row( modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    text = "Estat: ${recompensa.estat}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Text(
                    text = "Punts: ${recompensa.valor}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }

        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .background(color, RoundedCornerShape(4.dp))
                .fillMaxHeight()
                .width(80.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}
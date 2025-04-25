package cat.copernic.p3grup1.entrebicis.reward.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cat.copernic.p3grup1.entrebicis.core.enums.EstatRecompensa
import cat.copernic.p3grup1.entrebicis.core.models.Recompensa
import cat.copernic.p3grup1.entrebicis.core.theme.Assigned
import cat.copernic.p3grup1.entrebicis.core.theme.Primary
import cat.copernic.p3grup1.entrebicis.core.theme.Secondary
import cat.copernic.p3grup1.entrebicis.core.theme.Tertiary

@Composable
fun RewardCard(
    recompensa: Recompensa,
    onReservar: (Long) -> Unit,
    onClick: (Long) -> Unit,
    onRecollir: (Long) -> Unit,
    entregat: Boolean,
    onTancarDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorText: Pair<Color, String> = when (recompensa.estat) {
        EstatRecompensa.DISPONIBLE -> Primary to "Reservar"
        EstatRecompensa.ASSIGNADA -> Assigned to "Recollir"
        EstatRecompensa.RESERVADA -> Secondary to "Reservada"
        EstatRecompensa.RECOLLIDA -> Tertiary to "Recollida"
        else -> Color.LightGray to ""
    }

    val(color, text) = colorText
    val adjustText = when(text){
        "Reservada" -> 14.sp
        else -> 16.sp
    }

    val showDialog = remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(recompensa.id) }
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
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = recompensa.puntRecollida ?: "",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = recompensa.direccio ?: "",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
            if(showDialog.value) {
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
                                text = recompensa.puntRecollida,
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
                                }) {
                                    Text("TANCAR")
                                }
                            }
                        }
                    }
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
            if(recompensa.estat == EstatRecompensa.DISPONIBLE){
                Button(
                    onClick = { onReservar(recompensa.id) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp),
                    contentPadding = PaddingValues(horizontal = 0.dp)
                ) {
                    Text(
                        text = "Reservar",
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 16.sp),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }else if(recompensa.estat == EstatRecompensa.ASSIGNADA){
                Button(
                    onClick = { showDialog.value = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Assigned),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp),
                    contentPadding = PaddingValues(horizontal = 0.dp)
                ){
                    Text(
                        text = "Recollir",
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 16.sp),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }else {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = adjustText),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}
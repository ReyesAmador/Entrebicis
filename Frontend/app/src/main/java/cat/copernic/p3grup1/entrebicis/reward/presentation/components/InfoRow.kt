package cat.copernic.p3grup1.entrebicis.reward.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cat.copernic.p3grup1.entrebicis.core.theme.Secondary

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 6.dp)
            .fillMaxWidth()
    ) {
        Icon(icon, contentDescription = null, tint = Secondary)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "$label $value",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
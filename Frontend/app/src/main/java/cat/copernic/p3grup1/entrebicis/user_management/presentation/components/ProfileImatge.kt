package cat.copernic.p3grup1.entrebicis.user_management.presentation.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cat.copernic.p3grup1.entrebicis.R

@Composable
fun PerfilImage(base64: String?,
                modifier: Modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
) {

    val bitmap = remember(base64) {
        try {
            base64?.takeIf { it.isNotBlank() }?.let {
                val decoded = Base64.decode(it, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Imatge usuari",
            modifier = modifier
        )
    } else {
        Image(
            painter = painterResource(R.drawable.default_profile),
            contentDescription = "Imatge per defecte",
            modifier = modifier
        )
    }
}
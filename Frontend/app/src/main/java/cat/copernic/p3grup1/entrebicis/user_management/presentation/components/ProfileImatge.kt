package cat.copernic.p3grup1.entrebicis.user_management.presentation.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cat.copernic.p3grup1.entrebicis.R
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun PerfilImage(
    imageUrl: String?,
    token: String,
    modifier: Modifier = Modifier
        .size(100.dp)
        .clip(CircleShape)
) {

    if (imageUrl != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .addHeader("Authorization", "Bearer $token")
                .crossfade(true)
                .build(),
            contentDescription = "Imatge usuari",
            placeholder = painterResource(R.drawable.default_profile),
            error = painterResource(R.drawable.default_profile),
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
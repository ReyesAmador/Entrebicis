package cat.copernic.p3grup1.entrebicis.core.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.yield
import kotlin.math.roundToInt

@Composable
fun RefreshIndicator(
    isRefreshing: Boolean,
    modifier: Modifier = Modifier
){
    var visible by remember { mutableStateOf(false) }

    // Controla el desplazamiento vertical
    val offsetY = remember { Animatable(-60f) } // empieza por encima

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            offsetY.snapTo(-60f)
            visible = true
        } else {
            offsetY.animateTo(
                targetValue = -20f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            visible = false
        }
    }

    // Animación de entrada: cuando visible cambia a true, animamos hacia abajo
    LaunchedEffect(visible) {
        if (visible) {
            offsetY.animateTo(
                targetValue = 30f,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
        modifier = modifier
            .offset{ IntOffset(0, offsetY.value.roundToInt()) } // aplica el desplazamiento vertical animado
    ){
        Box(
            modifier = modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
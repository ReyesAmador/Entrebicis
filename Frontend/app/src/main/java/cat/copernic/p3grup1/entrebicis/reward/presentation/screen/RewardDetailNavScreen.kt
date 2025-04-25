package cat.copernic.p3grup1.entrebicis.reward.presentation.screen

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.p3grup1.entrebicis.reward.presentation.viewmodel.RewardViewModel
import cat.copernic.p3grup1.entrebicis.reward.presentation.viewmodel.provideRewardViewModelFactory

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RewardDetailNavScreen(
    id: Long,
    onBack: () -> Unit,
    onReservaSuccess: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: RewardViewModel = viewModel(
        factory = provideRewardViewModelFactory(context.applicationContext as Application)
    )
    val recompensa by viewModel.recompensa.collectAsState()
    val reservaSuccess by viewModel.reservaSuccess.collectAsState()
    val entregat by viewModel.recompensaEntregada.collectAsState()
    val error by viewModel.error.collectAsState()

    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }

    LaunchedEffect(id) {
        viewModel.carregarRecompensa(id)
    }

    // Mostrar feedback tras reserva
    LaunchedEffect(reservaSuccess, error) {
        reservaSuccess?.let { success ->
            if (success) {
                onReservaSuccess()
            } else {
                snackbarHostState.showSnackbar(error ?: "Error reservant la recompensa.")
            }
            viewModel.clearReservaStatus()
        }
    }

    recompensa?.let {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState){ data ->
                    val isSuccess = data.visuals.message.contains("correctament", ignoreCase = true)

                    Snackbar(
                        snackbarData = data,
                        containerColor = if (isSuccess) Color.Red else Color.Red,
                        contentColor = Color.White
                    )
                } }
        ) { padding ->
            RewardDetailScreen(
                recompensa = it,
                onBack = onBack,
                onReservar = { id -> viewModel.reservarRecompensa(id) },
                onRecollir = { id -> viewModel.recollirRecompensa(id) },
                entregat = entregat,
                onTancarDialog = { viewModel.resetEntrega() },
                modifier = Modifier.padding(padding)
            )
        }
    }
}
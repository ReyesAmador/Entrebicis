@file:OptIn(ExperimentalMaterial3Api::class)

package cat.copernic.p3grup1.entrebicis.reward.presentation.screen

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.p3grup1.entrebicis.R
import cat.copernic.p3grup1.entrebicis.core.components.RefreshIndicator
import cat.copernic.p3grup1.entrebicis.core.theme.Primary
import cat.copernic.p3grup1.entrebicis.reward.presentation.components.RewardCard
import cat.copernic.p3grup1.entrebicis.reward.presentation.viewmodel.RewardViewModel
import cat.copernic.p3grup1.entrebicis.reward.presentation.viewmodel.provideRewardViewModelFactory

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RewardScreen(
    onBack: () -> Unit,
    onRewardClick: (Long) -> Unit,
    mostraSnackbarReserva: Boolean
) {
    val context = LocalContext.current
    val viewModel: RewardViewModel = viewModel(
        factory = provideRewardViewModelFactory(context.applicationContext as Application)
    )
    val recompenses by viewModel.recompenses.collectAsState()
    val reservaSuccess by viewModel.reservaSuccess.collectAsState()
    val error by viewModel.error.collectAsState()
    val errorConnexio by viewModel.errorConnexio.collectAsState()
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }

    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val pullState = rememberPullToRefreshState()

    LaunchedEffect(Unit) {
        viewModel.carregarRecompenses()
    }

    LaunchedEffect(errorConnexio) {
        errorConnexio?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorConnexio()
        }
    }

    LaunchedEffect(mostraSnackbarReserva) {
        if (mostraSnackbarReserva) {
            snackbarHostState.showSnackbar("Recompensa reservada correctament!")
        }
    }

    LaunchedEffect(reservaSuccess, error) {
        reservaSuccess?.let { success ->
            if (success) {
                snackbarHostState.showSnackbar("Recompensa reservada correctament!")
            } else {
                snackbarHostState.showSnackbar(error ?: "Error inesperat al reservar")
            }
            viewModel.clearReservaStatus()
        }
    }

    LaunchedEffect(viewModel.recompensaEntregada.collectAsState().value) {
        if (viewModel.recompensaEntregada.value) {
            snackbarHostState.showSnackbar("Recompensa recollida correctament")
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState){ data ->
                val isSuccess = data.visuals.message.contains("correctament", ignoreCase = true)
                val backgroundColor = if(isSuccess) Color.Green else Color.Red

                Snackbar(
                    snackbarData = data,
                    containerColor = backgroundColor,
                    contentColor = Color.White
                )
            }
        }
    ) { padding ->

        Column(modifier = Modifier
            .fillMaxSize()
            .padding()) {
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
                        text = "Les teves recompenses",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }

            // Subtítulos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Recompenses",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Accions",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End
                )
            }

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.carregarRecompenses() },
                modifier = Modifier.fillMaxSize(),
                state = pullState,
                indicator = {
                    RefreshIndicator(
                        modifier = Modifier.align(Alignment.TopCenter),
                        isRefreshing = isRefreshing
                    )
                }
            ) {

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recompenses) { recompensa ->
                        RewardCard(
                            recompensa = recompensa,
                            onReservar = {
                                viewModel.reservarRecompensa(it)
                                viewModel.carregarRecompenses()
                            },
                            onClick = { onRewardClick(it) },
                            onRecollir = { viewModel.recollirRecompensa(it) },
                            entregat = viewModel.recompensaEntregada.collectAsState().value,
                            onTancarDialog = {
                                viewModel.resetEntrega()
                                viewModel.carregarRecompenses()
                            }
                        )
                    }
                }
            }
        }
    }
}
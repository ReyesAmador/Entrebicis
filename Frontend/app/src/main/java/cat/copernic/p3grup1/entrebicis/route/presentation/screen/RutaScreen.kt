package cat.copernic.p3grup1.entrebicis.route.presentation.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.p3grup1.entrebicis.R
import cat.copernic.p3grup1.entrebicis.core.components.RefreshIndicator
import cat.copernic.p3grup1.entrebicis.route.presentation.components.RutaCard
import cat.copernic.p3grup1.entrebicis.route.presentation.viewmodel.RutaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RutaScreen(
    viewModel: RutaViewModel = viewModel(),
    onBack: () -> Unit,
    onRutaClick: (Long) -> Unit
) {
    val rutes by viewModel.rutesFinalitzades.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val errorConnexio by viewModel.errorConnexio.collectAsState()

    val pullState  = rememberPullToRefreshState()

    LaunchedEffect(Unit) {
        viewModel.carregarRutesFinalitzades()
    }

    LaunchedEffect(errorConnexio) {
        errorConnexio?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorConnexio()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(184.dp)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 8.dp, top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Tornar",
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
                        text = "Aquestes són les teves rutes",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White
                    )
                }
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Tens un total de: ${rutes.size} rutes",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Rutes", style = MaterialTheme.typography.headlineSmall)
                    Text("Saldo", style = MaterialTheme.typography.headlineSmall)
                }

                Spacer(Modifier.height(8.dp))

                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = { viewModel.carregarRutesFinalitzades() },
                    modifier = Modifier.fillMaxSize(),
                    state = pullState,
                    indicator = {
                        RefreshIndicator(
                            modifier = Modifier.align(Alignment.TopCenter),
                            isRefreshing = isRefreshing
                        )
                    }
                ) {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(rutes) { ruta ->
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RutaCard(
                                    ruta,
                                    modifier = Modifier.weight(1f),
                                    onClick = { onRutaClick(ruta.id) })
                                Spacer(Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (ruta.validada) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.secondary
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "${"%.1f".format(ruta.saldo)}p",
                                        color = Color.White,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
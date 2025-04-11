@file:OptIn(ExperimentalMaterial3Api::class)

package cat.copernic.p3grup1.entrebicis.reward.presentation.screen

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cat.copernic.p3grup1.entrebicis.reward.presentation.components.RewardCard
import cat.copernic.p3grup1.entrebicis.reward.presentation.viewmodel.RewardViewModel
import cat.copernic.p3grup1.entrebicis.reward.presentation.viewmodel.provideRewardViewModelFactory

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RewardScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: RewardViewModel = viewModel(
        factory = provideRewardViewModelFactory(context.applicationContext as Application)
    )
    val recompenses by viewModel.recompenses.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.carregarRecompenses()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Les teves recompenses") }, navigationIcon = {
            IconButton(onClick = { /* navegar enrere */ }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        })

        if (error != null) {
            Text(
                text = error ?: "",
                color = Color.Red,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(recompenses) { recompensa ->
                RewardCard(recompensa = recompensa)
            }
        }
    }
}
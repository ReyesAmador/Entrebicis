package cat.copernic.p3grup1.entrebicis.home.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cat.copernic.p3grup1.entrebicis.R
import cat.copernic.p3grup1.entrebicis.home.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    homeViewModel: HomeViewModel = viewModel()) {
    val username = homeViewModel.username
    val punts = homeViewModel.punts

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(48.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_black),
            contentDescription = "Logo Entrebicis"
        )


        Text("Benvingut, $username", style = MaterialTheme.typography.headlineLarge)

        Spacer(Modifier.height(16.dp))

        Text(
            "Tens $punts punts",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(50))
                .padding(horizontal = 24.dp, vertical = 8.dp)
        )

        Spacer(Modifier.height(210.dp))

        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .height(38.dp)
                .width(240.dp)
        ) {
            Text("INICIAR RUTA", style = MaterialTheme.typography.labelLarge, color = Color.White)
        }
        Spacer(Modifier.height(90.dp))

        Button(
            onClick = {
                homeViewModel.logout()
                onLogout()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .height(38.dp)
                .width(240.dp)
        ) {
            Text("TANCAR SESSIÓ", style = MaterialTheme.typography.labelLarge, color = Color.White)
        }
    }
}
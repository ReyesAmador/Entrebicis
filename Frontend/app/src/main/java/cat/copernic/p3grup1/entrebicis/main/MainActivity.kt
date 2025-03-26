package cat.copernic.p3grup1.entrebicis.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import cat.copernic.p3grup1.entrebicis.core.components.BottomNavBar
import cat.copernic.p3grup1.entrebicis.core.navigation.AppNavGraph
import cat.copernic.p3grup1.entrebicis.core.theme.EntrebicisTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EntrebicisTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomNavBar(navController) }
                ){  innerPadding ->
                    AppNavGraph(navController, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

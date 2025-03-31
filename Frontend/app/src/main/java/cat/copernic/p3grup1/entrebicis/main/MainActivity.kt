package cat.copernic.p3grup1.entrebicis.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
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
                val navBackStackEntry = navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry.value?.destination?.route

                val showBottomBar = currentRoute !in listOf("login", "splash", "forgot-password")
                Scaffold(
                    bottomBar = {
                        if(showBottomBar)
                            BottomNavBar(navController)
                    }
                ){  innerPadding ->
                    AppNavGraph(navController, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

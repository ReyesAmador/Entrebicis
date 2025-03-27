package cat.copernic.p3grup1.entrebicis.core.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cat.copernic.p3grup1.entrebicis.core.components.BottomNavItem
import cat.copernic.p3grup1.entrebicis.home.presentation.screen.HomeScreen
import cat.copernic.p3grup1.entrebicis.user_management.presentation.screen.LoginScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {
        composable("login"){
            LoginScreen (
                onLoginSuccess = {
                    navController.navigate(BottomNavItem.Home.route) {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable(BottomNavItem.Home.route) { HomeScreen() }
        composable(BottomNavItem.Route.route) { /* TODO */ }
        composable(BottomNavItem.Reward.route) { /* TODO */ }
        composable(BottomNavItem.Profile.route) { /* TODO */ }
    }
}
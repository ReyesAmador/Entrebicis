package cat.copernic.p3grup1.entrebicis.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cat.copernic.p3grup1.entrebicis.core.components.BottomNavItem
import cat.copernic.p3grup1.entrebicis.home.presentation.screen.HomeScreen

@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier) {
        composable(BottomNavItem.Home.route) { HomeScreen() }
        composable(BottomNavItem.Route.route) { /* TODO */ }
        composable(BottomNavItem.Reward.route) { /* TODO */ }
        composable(BottomNavItem.Profile.route) { /* TODO */ }
    }
}
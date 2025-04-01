package cat.copernic.p3grup1.entrebicis.core.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cat.copernic.p3grup1.entrebicis.core.components.BottomNavItem
import cat.copernic.p3grup1.entrebicis.home.presentation.screen.HomeScreen
import cat.copernic.p3grup1.entrebicis.splash.presentation.SplashScreen
import cat.copernic.p3grup1.entrebicis.user_management.presentation.screen.LoginScreen
import cat.copernic.p3grup1.entrebicis.user_management.presentation.screen.recovery_password.ForgotPasswordScreen
import cat.copernic.p3grup1.entrebicis.user_management.presentation.screen.recovery_password.ResetPasswordScreen
import cat.copernic.p3grup1.entrebicis.user_management.presentation.screen.recovery_password.ValidateCodeScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier
    ) {

        composable("splash") {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(BottomNavItem.Home.route) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("login"){
            LoginScreen (
                onLoginSuccess = {
                    navController.navigate(BottomNavItem.Home.route) {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onForgotPassword = { navController.navigate("forgot-password")}
            )
        }

        composable("forgot-password"){
            ForgotPasswordScreen( onCodeSent = { email ->
                navController.navigate("validate-code/$email")
            })
        }

        composable("validate-code/{email}",
            arguments = listOf(navArgument("email") { defaultValue = ""; nullable = false })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ValidateCodeScreen(
                email = email,
                onCodeValidated = { codi ->
                navController.navigate("reset-password/$email/$codi")
            })
        }

        composable("reset-password/{email}/{codi}",
            arguments = listOf(
                navArgument("email") { defaultValue = ""; nullable = false },
                navArgument("codi") { defaultValue = ""; nullable = false }
                )
            ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val codi = backStackEntry.arguments?.getString("codi") ?: ""
            ResetPasswordScreen(
                email = email,
                codi = codi,
                onPasswordChanged = {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("successMessage", "contrasenya")

                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }

        composable(BottomNavItem.Home.route) {
            HomeScreen(
                onLogout = {
                    navController.navigate("login"){
                        popUpTo("splash") { inclusive = true }
                    }
                }
            ) }
        composable(BottomNavItem.Route.route) { /* TODO */ }
        composable(BottomNavItem.Reward.route) { /* TODO */ }
        composable(BottomNavItem.Profile.route) { /* TODO */ }
    }
}
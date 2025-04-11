package cat.copernic.p3grup1.entrebicis.core.navigation

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cat.copernic.p3grup1.entrebicis.core.components.BottomNavItem
import cat.copernic.p3grup1.entrebicis.home.presentation.screen.HomeScreen
import cat.copernic.p3grup1.entrebicis.home.presentation.viewmodel.HomeViewModel
import cat.copernic.p3grup1.entrebicis.route.presentation.screen.DetallRutaScreen
import cat.copernic.p3grup1.entrebicis.route.presentation.screen.RutaScreen
import cat.copernic.p3grup1.entrebicis.route.presentation.viewmodel.RutaViewModel
import cat.copernic.p3grup1.entrebicis.route.presentation.viewmodel.detallRutaViewModelFactory
import cat.copernic.p3grup1.entrebicis.splash.presentation.SplashScreen
import cat.copernic.p3grup1.entrebicis.user_management.presentation.screen.LoginScreen
import cat.copernic.p3grup1.entrebicis.user_management.presentation.screen.UserProfileScreen
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

        composable("route/detallRuta") {
            DetallRutaScreen(onBack = { navController.popBackStack() })
        }

        composable("route/detallRuta/{idRuta}", arguments = listOf(
            navArgument("idRuta") { type = NavType.LongType }
        )) { backStackEntry ->
            val idRuta = backStackEntry.arguments?.getLong("idRuta")
            DetallRutaScreen(idRuta = idRuta, onBack = { navController.popBackStack() })
        }

        composable("forgot-password"){
            ForgotPasswordScreen( onCodeSent = { email ->
                navController.navigate("validate-code/$email")
            },
                onBack = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("validate-code/{email}",
            arguments = listOf(navArgument("email") { defaultValue = ""; nullable = false })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ValidateCodeScreen(
                email = email,
                onBack = { navController.popBackStack()},
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
                onBack = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
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
            val viewModel: HomeViewModel = viewModel()
            val mostrarDetallRuta by viewModel.mostrarDetallRuta.collectAsState()

            // Navegación automática a detallRuta
            LaunchedEffect(mostrarDetallRuta) {
                if (mostrarDetallRuta) {
                    navController.navigate("route/detallRuta")
                    viewModel.resetNavegacio() // ← esto lo añades para que no se repita
                }
            }
            HomeScreen(
                onLogout = {
                    navController.navigate("login"){
                        popUpTo("splash") { inclusive = true }
                    }
                }
            ) }
        composable(BottomNavItem.Route.route) {
            val routeViewModel: RutaViewModel = viewModel(
                factory = detallRutaViewModelFactory(LocalContext.current.applicationContext as Application)
            )

            RutaScreen(
                viewModel = routeViewModel,
                onBack = {
                    navController.popBackStack()
                },
                onRutaClick = {id -> navController.navigate("route/detallRuta/$id")}
            )
        }
        composable(BottomNavItem.Reward.route) { /* TODO */ }
        composable(BottomNavItem.Profile.route) {
            UserProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
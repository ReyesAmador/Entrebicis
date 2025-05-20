package cat.copernic.p3grup1.entrebicis.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * Composable que mostra la barra de navegació inferior amb els ítems definits a [BottomNavItem].
 *
 * Aquesta barra permet navegar entre diferents pantalles de l’aplicació.
 *
 * @param navController Controlador de navegació utilitzat per gestionar els canvis de ruta.
 */
@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Route,
        BottomNavItem.Reward,
        BottomNavItem.Profile
    )

    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        items.forEach { item ->
            val isSelected = currentDestination?.route?.startsWith(item.route) == true

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route){
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(103.dp)
                            .height(80.dp)
                            .background(
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.25f)
                                else
                                    MaterialTheme.colorScheme.surface,
                            )
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                modifier = Modifier.weight(1f),
                label = null, // Si no quieres texto, o pon `Text(item.label)`
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent, // sin fondo por defecto
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.tertiary
                )
            )
        }
    }
}
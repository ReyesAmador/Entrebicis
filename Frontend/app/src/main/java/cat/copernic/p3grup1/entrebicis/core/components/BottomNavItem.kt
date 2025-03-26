package cat.copernic.p3grup1.entrebicis.core.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Inici")
    object Route : BottomNavItem("route", Icons.AutoMirrored.Filled.DirectionsBike, "Rutes")
    object Reward : BottomNavItem("reward", Icons.Default.CardGiftcard, "Recompenses")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Perfil")
}
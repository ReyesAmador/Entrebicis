package cat.copernic.p3grup1.entrebicis.core.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Classe segellada que defineix els ítems de la barra de navegació inferior.
 *
 * @property route Ruta de navegació associada a l'element.
 * @property icon Icona que es mostra a la barra de navegació.
 * @property label Etiqueta descriptiva de l'element.
 */
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    /** Element de navegació per a la pantalla d'inici. */
    object Home : BottomNavItem("home", Icons.Default.Home, "Inici")

    /** Element de navegació per a la pantalla de rutes. */
    object Route : BottomNavItem("route", Icons.AutoMirrored.Filled.DirectionsBike, "Rutes")

    /** Element de navegació per a la pantalla de recompenses. */
    object Reward : BottomNavItem("reward", Icons.Default.CardGiftcard, "Recompenses")

    /** Element de navegació per a la pantalla de perfil d’usuari. */
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Perfil")
}
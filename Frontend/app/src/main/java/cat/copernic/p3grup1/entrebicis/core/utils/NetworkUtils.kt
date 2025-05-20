package cat.copernic.p3grup1.entrebicis.core.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Comprova si el dispositiu té connexió a Internet activa.
 *
 * Aquesta funció utilitza [ConnectivityManager] per accedir a l'estat de la xarxa actual
 * i verificar si hi ha capacitat de connexió a Internet.
 *
 * @param context Context de l'aplicació per accedir als serveis del sistema.
 * @return `true` si hi ha una connexió a Internet activa, `false` altrament.
 */
fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
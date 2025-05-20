package cat.copernic.p3grup1.entrebicis.core.utils

/**
 * Dona format al temps en mil·lisegons com una cadena de text MM:SS.
 *
 * Aquesta funció és útil per mostrar la durada d'una ruta o cronòmetre
 * en format llegible per l'usuari.
 *
 * @param millis Temps en mil·lisegons.
 * @return Una cadena amb el format "MM:SS".
 */
fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
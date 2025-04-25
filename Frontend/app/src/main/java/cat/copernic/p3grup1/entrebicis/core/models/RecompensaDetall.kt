package cat.copernic.p3grup1.entrebicis.core.models

data class RecompensaDetall(
    val id: Long,
    val descripcio: String,
    val dataCreacio: String,
    val dataReserva: String,
    val dataAssignacio: String,
    val dataRecollida: String,
    val nomUsuari: String,
    val nomPunt: String,
    val direccio: String,
    val estat: String,
    val observacions: String
)

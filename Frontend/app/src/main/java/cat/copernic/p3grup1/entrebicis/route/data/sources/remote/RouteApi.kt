package cat.copernic.p3grup1.entrebicis.route.data.sources.remote

import cat.copernic.p3grup1.entrebicis.core.models.Usuari
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import java.time.LocalDateTime

data class PuntGpsDto(
    val latitud: Double,
    val longitud: Double,
    val temps: Long
)

data class RutaAmbPuntsDto(
    val distanciaTotal: Double,
    val tempsTotal: String,
    val velocitatMitjana: Double,
    val velocitatMaxima: Double,
    val punts: List<PuntGpsDto>
)

data class RutaSensePuntsDto(
    val usuari: Usuari,
    val id: Long,
    val inici: String,
    val temps_total: String,
    val estat: Boolean,
    val validada: Boolean,
    val km_total: Double,
    val velocitat_mitjana: Double,
    val velocitat_max: Double,
    val saldo: Double
)

data class RutaDTO(
    val id: Long,
    val inici: String,
    val estat: Boolean,
    val validada: Boolean,
    val nomUsuari: String
)

interface RouteApi {

    @POST("ruta/punt")
    suspend fun enviarPuntGps(@Body punt: PuntGpsDto): Response<Void>

    @GET("parametres/temps-maxim-aturada")
    suspend fun getTempsMaximAturada(): Response<Int>

    @PATCH("ruta/finalitzar")
    suspend fun finalitzarRuta(): Response<Void>

    @GET("ruta/detall")
    suspend fun getRutaAmbPunts(): Response<RutaAmbPuntsDto>

    @GET("ruta/finalitzades")
    suspend fun getRutesFinalitzdes(@Header("Authorization") token: String): Response<List<RutaSensePuntsDto>>

    @GET("ruta/detall/{id}")
    suspend fun getDetallRuta(@Path("id") id: Long): Response<RutaAmbPuntsDto>

    @POST("ruta/iniciar")
    suspend fun iniciarRuta(@Header("Authorization") token: String): Response<RutaDTO>
}
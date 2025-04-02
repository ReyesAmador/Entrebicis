package cat.copernic.p3grup1.entrebicis.route.data.sources.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

data class PuntGpsDto(
    val latitud: Double,
    val longitud: Double,
    val temps: Long
)

interface RouteApi {

    @POST("ruta/punt")
    suspend fun enviarPuntGps(@Body punt: PuntGpsDto): Response<Void>

    @GET("parametres/temps-maxim-aturada")
    suspend fun getTempsMaximAturada(): Response<Int>

    @PATCH("ruta/finalitzar")
    suspend fun finalitzarRuta(): Response<Void>
}
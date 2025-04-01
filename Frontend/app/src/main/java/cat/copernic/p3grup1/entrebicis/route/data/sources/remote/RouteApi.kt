package cat.copernic.p3grup1.entrebicis.route.data.sources.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

data class PuntGpsDto(
    val latitud: Double,
    val longitud: Double,
    val temps: Long
)

interface RouteApi {

    @POST("ruta/punt/{email}")
    suspend fun enviarPuntGps(@Path("email") email:String, @Body punt: PuntGpsDto): Response<Void>
}
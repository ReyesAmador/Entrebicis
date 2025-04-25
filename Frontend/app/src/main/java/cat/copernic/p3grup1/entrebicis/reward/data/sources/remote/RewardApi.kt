package cat.copernic.p3grup1.entrebicis.reward.data.sources.remote

import cat.copernic.p3grup1.entrebicis.core.models.Recompensa
import cat.copernic.p3grup1.entrebicis.core.models.RecompensaDetall
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface RewardApi {

    @GET("recompenses")
    suspend fun getRecompenses(@Header("Authorization") token: String): Response<List<Recompensa>>

    @POST("recompenses/reservar/{id}")
    suspend fun reservarRecompensa(
        @Path("id") id: Long,
        @Header("Authorization") token: String
    ): Response<Void>

    @GET("recompenses/{id}")
    suspend fun getRecompensa(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<RecompensaDetall>

    @PATCH("recompenses/recollir/{id}")
    suspend fun recollirRecompensa(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Unit>
}
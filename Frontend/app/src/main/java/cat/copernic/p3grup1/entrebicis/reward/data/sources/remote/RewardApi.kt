package cat.copernic.p3grup1.entrebicis.reward.data.sources.remote

import cat.copernic.p3grup1.entrebicis.core.models.Recompensa
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
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
}
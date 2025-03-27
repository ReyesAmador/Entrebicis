package cat.copernic.p3grup1.entrebicis.user_management.data.sources.remote

import cat.copernic.p3grup1.entrebicis.core.models.Usuari
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

data class LoginRequest(
    val email: String,
    val paraula: String
)

data class LoginResponse(
    val token: String
)

interface UserApi {

    @POST("api/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("api/usuari")
    suspend fun getUsuari(@Header("Authorization") token: String): Response<Usuari>
}
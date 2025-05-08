package cat.copernic.p3grup1.entrebicis.user_management.data.sources.remote

import cat.copernic.p3grup1.entrebicis.core.models.Usuari
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST

data class LoginRequest(
    val email: String,
    val paraula: String
)

data class LoginResponse(
    val token: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class ValidateCodeRequest(
    val email: String,
    val codi: String
)

data class ResetPasswordRequest(
    val email: String,
    val codi: String,
    val novaContrasenya: String
)

data class CanviContrasenyaRequest(
    val actual: String,
    val nova: String,
    val repetirNova: String
)

interface UserApi {

    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("usuari")
    suspend fun getUsuari(@Header("Authorization") token: String): Response<Usuari>

    @POST("forgot-pass")
    suspend fun forgotPassword(@Body body: ForgotPasswordRequest): Response<Void>

    @POST("validate-code")
    suspend fun validarCodi(@Body body: ValidateCodeRequest): Response<Void>

    @POST("reset-pass")
    suspend fun resetPass(@Body body: ResetPasswordRequest): Response<Void>

    @PATCH("usuari/actualitzar")
    suspend fun actualitzarUsuari(@Header("Authorization") token: String, @Body usuari: Usuari): Response<Unit>

    @POST("usuari/canvi-pass")
    suspend fun canviContrasenya(
        @Header("Authorization") token: String,
        @Body request: CanviContrasenyaRequest
    ): Response<ResponseBody>

    @GET("usuari/imatge")
    suspend fun getUserImage(
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @POST("usuari/imatge")
    suspend fun uploadUserImage(
        @Header("Authorization") token: String,
        @Body image: RequestBody
    ): Response<String>

}
package cat.copernic.p3grup1.entrebicis.user_management.data.repositories

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import cat.copernic.p3grup1.entrebicis.core.models.Usuari
import cat.copernic.p3grup1.entrebicis.core.network.RetrofitClient
import cat.copernic.p3grup1.entrebicis.user_management.data.sources.remote.CanviContrasenyaRequest
import cat.copernic.p3grup1.entrebicis.user_management.data.sources.remote.ForgotPasswordRequest
import cat.copernic.p3grup1.entrebicis.user_management.data.sources.remote.LoginRequest
import cat.copernic.p3grup1.entrebicis.user_management.data.sources.remote.LoginResponse
import cat.copernic.p3grup1.entrebicis.user_management.data.sources.remote.ResetPasswordRequest
import cat.copernic.p3grup1.entrebicis.user_management.data.sources.remote.UserApi
import cat.copernic.p3grup1.entrebicis.user_management.data.sources.remote.ValidateCodeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response

class LoginRepo(private val api: UserApi) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun login (email: String, paraula: String) : Result<LoginResponse>{
        return try {
            val response = api.login(LoginRequest(email, paraula))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconegut"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getUsuari(token: String): Result<Usuari> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getUsuari("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun forgotPass(email: String): Result<Unit>{
        return try{
            val response = api.forgotPassword(ForgotPasswordRequest(email))
            if(response.isSuccessful){
                Result.success(Unit)
            }else{
                val errorBody = response.errorBody()?.string()
                // Intenta extraer el mensaje de error del backend si es JSON
                val msg = try {
                    JSONObject(errorBody ?: "").getString("message")
                } catch (e: Exception) {
                    // Si no es JSON, usa el string plano
                    errorBody ?: "Error ${response.code()}"
                }
                Result.failure(Exception(msg))
            }
        }catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun validateCode(email: String, codi: String): Result<Unit> {
        return try {
            val response = api.validarCodi(ValidateCodeRequest(email, codi))
            if (response.isSuccessful) Result.success(Unit)
            else{
                val errorBody = response.errorBody()?.string()
                // Intenta extraer el mensaje de error del backend si es JSON
                val msg = try {
                    JSONObject(errorBody ?: "").getString("message")
                } catch (e: Exception) {
                    // Si no es JSON, usa el string plano
                    errorBody ?: "Error ${response.code()}"
                }
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String, codi: String, novaContrasenya: String): Result<Unit> {
        return try {
            val response = api.resetPass(ResetPasswordRequest(email, codi, novaContrasenya))
            if (response.isSuccessful) Result.success(Unit)
            else {
                val errorBody = response.errorBody()?.string()
                // Intenta extraer el mensaje de error del backend si es JSON
                val msg = try {
                    JSONObject(errorBody ?: "").getString("message")
                } catch (e: Exception) {
                    // Si no es JSON, usa el string plano
                    errorBody ?: "Error ${response.code()}"
                }
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadUserImage(token: String, imageBytes: ByteArray): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody: RequestBody = imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                val response = api.uploadUserImage("Bearer $token", requestBody)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception(response.errorBody()?.string() ?: "Error pujant imatge"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getUserImage(token: String): Response<ResponseBody> {
        return withContext(Dispatchers.IO) {
            api.getUserImage("Bearer $token")
        }
    }

    suspend fun actualitzarUsuari(token: String, usuari: Usuari): Result<Unit>{
        return withContext(Dispatchers.IO){
            try {
                val res = api.actualitzarUsuari("Bearer $token", usuari)
                if (res.isSuccessful){
                    Result.success(Unit)
                }
                else {
                    val errorMsg = res.errorBody()?.string() ?: "Error desconegut"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun canviContrasenya(token: String, request: CanviContrasenyaRequest): Result<String>{
        return try {
            val response = api.canviContrasenya("Bearer $token", request)
            if (response.isSuccessful && response.body() != null){
                val msg = response.body()!!.string()
                Result.success(msg)
            }else{
                Result.failure(Exception(response.errorBody()?.string() ?: "Error canviant contrasenya"))
            }
        }catch (e: Exception) {
            Result.failure(e)
        }
    }
}
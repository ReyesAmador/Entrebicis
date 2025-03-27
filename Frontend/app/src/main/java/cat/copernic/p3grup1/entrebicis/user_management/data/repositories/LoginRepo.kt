package cat.copernic.p3grup1.entrebicis.user_management.data.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import cat.copernic.p3grup1.entrebicis.core.models.Usuari
import cat.copernic.p3grup1.entrebicis.core.network.RetrofitClient
import cat.copernic.p3grup1.entrebicis.user_management.data.sources.remote.LoginRequest
import cat.copernic.p3grup1.entrebicis.user_management.data.sources.remote.LoginResponse
import cat.copernic.p3grup1.entrebicis.user_management.data.sources.remote.UserApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class LoginRepo(private val api: UserApi) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun login (email: String, paraula: String) : Result<LoginResponse>{
        return withContext(Dispatchers.IO){
            try{
                val response: Response<LoginResponse> = api.login(LoginRequest(email, paraula))
                if(response.isSuccessful && response.body() != null){
                    Result.success(response.body()!!)
                }else{
                    Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
                }
            }catch (e: Exception){
                Result.failure(e)
            }
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
}
package cat.copernic.p3grup1.entrebicis.route.data.repositories

import cat.copernic.p3grup1.entrebicis.route.data.sources.remote.PuntGpsDto
import cat.copernic.p3grup1.entrebicis.route.data.sources.remote.RouteApi
import cat.copernic.p3grup1.entrebicis.route.data.sources.remote.RutaAmbPuntsDto
import cat.copernic.p3grup1.entrebicis.route.data.sources.remote.RutaSensePuntsDto

class RouteRepo(private val api: RouteApi) {

    suspend fun enviarPunt(punt: PuntGpsDto): Result<Unit>{
        return try{
            val response = api.enviarPuntGps(punt)
            if(response.isSuccessful){
                Result.success(Unit)
            }else{
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        }catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTempsMaximAturada(): Result<Int> {
        return try {
            val res = api.getTempsMaximAturada()
            if (res.isSuccessful && res.body() != null) Result.success(res.body()!!)
            else Result.failure(Exception("Error ${res.code()}: ${res.message()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun finalitzarRuta(): Result<Unit>{
        return try {
            val response = api.finalitzarRuta()
            if(response.isSuccessful){
                Result.success(Unit)
            }else{
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        }catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRutaAmbPunts() : Result<RutaAmbPuntsDto>{
        return try {
            val response = api.getRutaAmbPunts()
            if(response.isSuccessful && response.body() != null){
                Result.success(response.body()!!)
            }else{
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRutesFinalitzades(token: String) : Result<List<RutaSensePuntsDto>>{
        return try{
            val response = api.getRutesFinalitzdes("Bearer $token")
            if(response.isSuccessful && response.body() != null){
                Result.success(response.body()!!)
            }else{
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        }catch (e: Exception) {
            Result.failure(e)
        }
    }
}
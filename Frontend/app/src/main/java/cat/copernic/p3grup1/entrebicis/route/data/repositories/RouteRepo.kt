package cat.copernic.p3grup1.entrebicis.route.data.repositories

import cat.copernic.p3grup1.entrebicis.route.data.sources.remote.PuntGpsDto
import cat.copernic.p3grup1.entrebicis.route.data.sources.remote.RouteApi

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
}
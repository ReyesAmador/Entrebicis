package cat.copernic.p3grup1.entrebicis.route.data.repositories

import cat.copernic.p3grup1.entrebicis.route.data.sources.remote.PuntGpsDto
import cat.copernic.p3grup1.entrebicis.route.data.sources.remote.RouteApi
import cat.copernic.p3grup1.entrebicis.route.data.sources.remote.RutaAmbPuntsDto
import cat.copernic.p3grup1.entrebicis.route.data.sources.remote.RutaDTO
import cat.copernic.p3grup1.entrebicis.route.data.sources.remote.RutaSensePuntsDto

class RouteRepo(private val api: RouteApi) {

    suspend fun iniciarRuta(token: String): Result<RutaDTO>{
        return try {
            val response = api.iniciarRuta("Bearer $token")
            if (response.isSuccessful){
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Cos del missatge buit"))
            }else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconegut"
                Result.failure(Exception(errorMsg))
            }
        }catch (e: Exception) {
            Result.failure(e)
        }
    }

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

    suspend fun getGpsParams(): Result<Pair<Long, Float>> {
        return try {
            val res = api.getGpsParams()
            if (res.isSuccessful && res.body() != null) {
                val body = res.body()!!
                val interval = (body["intervalMs"] as? Double)?.toLong() ?: 5000L
                val distancia = (body["distanciaMinima"] as? Double)?.toFloat() ?: 10f
                Result.success(interval to distancia)
            } else Result.failure(Exception("Error ${res.code()}: ${res.message()}"))
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

    suspend fun getDetallRutaEspecifica(id: Long): Result<RutaAmbPuntsDto> {
        return try {
            val resposta = api.getDetallRuta(id)
            if (resposta.isSuccessful) Result.success(resposta.body()!!)
            else Result.failure(Exception("Error ${resposta.code()}: ${resposta.message()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
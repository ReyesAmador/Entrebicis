package cat.copernic.p3grup1.entrebicis.reward.data.repositories

import cat.copernic.p3grup1.entrebicis.core.models.Recompensa
import cat.copernic.p3grup1.entrebicis.reward.data.sources.remote.RewardApi

class RewardRepo(private val api: RewardApi) {

    suspend fun getRecompenses(token: String): Result<List<Recompensa>>{
        return try{
            val response = api.getRecompenses("Bearer $token")
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
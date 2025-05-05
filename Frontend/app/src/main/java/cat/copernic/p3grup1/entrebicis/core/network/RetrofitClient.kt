package cat.copernic.p3grup1.entrebicis.core.network

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object RetrofitClient {

    private const val BASE_URL = "https://entrebicis.ddns.net:8443/api/"

    // 🔹 Formato estándar para LocalDate (yyyy-MM-dd)
    @RequiresApi(Build.VERSION_CODES.O)
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    // 🔹 Serializador: Convierte LocalDate a String (para enviar al backend)
    @RequiresApi(Build.VERSION_CODES.O)
    private val localDateSerializer = JsonSerializer<LocalDate> { src, _, _ ->
        JsonPrimitive(src.format(dateFormatter))
    }

    // 🔹 Deserializador: Convierte String a LocalDate (cuando lo recibimos del backend)
    @RequiresApi(Build.VERSION_CODES.O)
    private val localDateDeserializer = JsonDeserializer { json, _, _ ->
        json.asJsonPrimitive?.asString?.let { LocalDate.parse(it, dateFormatter) }
    }

    // 🔹 Configuración de Gson con soporte para LocalDate
    @RequiresApi(Build.VERSION_CODES.O)
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, localDateSerializer)
        .registerTypeAdapter(LocalDate::class.java, localDateDeserializer)
        .create()

    @Volatile
    private var retrofitInstance: Retrofit? = null

    // Nueva función para obtener Retrofit con token JWT
    @RequiresApi(Build.VERSION_CODES.O)
    fun getInstance(context: Context): Retrofit {
        if (retrofitInstance == null) {
            synchronized(this) {
                val client = OkHttpClient.Builder()
                    .addInterceptor(Interceptor { chain ->
                        val prefs = context.getSharedPreferences("usuari_prefs", Context.MODE_PRIVATE)
                        val token = prefs.getString("token", null)
                        val request = chain.request().newBuilder()
                        token?.let {
                            request.addHeader("Authorization", "Bearer $it")
                        }
                        chain.proceed(request.build())
                    })
                    .build()

                retrofitInstance = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }
        }
        return retrofitInstance!!
    }

    fun reset() {
        retrofitInstance = null
    }
}
package cat.copernic.p3grup1.entrebicis.core.network

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/api/"

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

    // 🔹 Construcción de Retrofit con Gson personalizado
    @RequiresApi(Build.VERSION_CODES.O)
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson)) // 🔹 Usa Gson con LocalDate
        .build()
}
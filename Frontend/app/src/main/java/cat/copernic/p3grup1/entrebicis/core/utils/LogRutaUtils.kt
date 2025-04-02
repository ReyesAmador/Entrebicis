package cat.copernic.p3grup1.entrebicis.core.utils

import android.content.Context
import android.util.Log
import java.io.File

object LogRutaUtils {

    private const val FILE_NAME = "log_rutes.txt"

    fun appendLog(context: Context, text: String) {
        try {
            val file = File(context.filesDir, FILE_NAME)
            file.appendText("${System.currentTimeMillis()} - $text\n")
        } catch (e: Exception) {
            Log.e("LOG_FILE", "Error escrivint al log: ${e.message}")
        }
    }

    fun readLog(context: Context): String {
        return try {
            val file = File(context.filesDir, FILE_NAME)
            if (file.exists()) file.readText() else "No hi ha cap registre."
        } catch (e: Exception) {
            "Error llegint el log: ${e.message}"
        }
    }

    fun clearLog(context: Context) {
        try {
            val file = File(context.filesDir, FILE_NAME)
            if (file.exists()) file.writeText("")
        } catch (e: Exception) {
            Log.e("LOG_FILE", "Error netejant el log: ${e.message}")
        }
    }
}
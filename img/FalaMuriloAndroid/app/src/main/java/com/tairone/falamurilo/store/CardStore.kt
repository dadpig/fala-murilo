package com.tairone.falamurilo.store

import android.app.Application
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.tairone.falamurilo.models.AppLanguage
import java.io.File

class CardStore(app: Application) : AndroidViewModel(app) {

    private val prefs: SharedPreferences =
        app.getSharedPreferences("fala_murilo", 0)
    private val filesDir: File = app.filesDir

    @set:JvmName("setLanguageState")
    var language by mutableStateOf(
        AppLanguage.fromCode(prefs.getString("language", null) ?: AppLanguage.detected().code)
    )
        private set

    var version by mutableIntStateOf(0)
        private set

    fun setLanguage(lang: AppLanguage) {
        language = lang
        prefs.edit().putString("language", lang.code).apply()
    }

    fun customLabel(cardId: String): String? =
        prefs.getString(labelKey(cardId), null)

    fun setLabel(cardId: String, text: String) {
        prefs.edit().apply {
            if (text.isBlank()) remove(labelKey(cardId))
            else putString(labelKey(cardId), text.trim())
        }.apply()
        bump()
    }

    fun displayLabel(cardId: String, default: String): String =
        customLabel(cardId) ?: default

    private fun labelKey(cardId: String) = "label:${cardId}:${language.code}"

    private fun photoFile(cardId: String) = File(filesDir, "photos/$cardId.jpg")

    fun hasPhoto(cardId: String) = photoFile(cardId).exists()

    fun loadPhoto(cardId: String): Bitmap? {
        val f = photoFile(cardId)
        return if (f.exists()) BitmapFactory.decodeFile(f.path) else null
    }

    fun savePhoto(cardId: String, bitmap: Bitmap) {
        File(filesDir, "photos").mkdirs()
        photoFile(cardId).outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
        }
        bump()
    }

    fun clearPhoto(cardId: String) {
        photoFile(cardId).delete()
        bump()
    }

    fun audioFile(cardId: String) = File(filesDir, "audio/$cardId.m4a")

    fun hasAudio(cardId: String) = audioFile(cardId).exists()

    fun clearAudio(cardId: String) {
        audioFile(cardId).delete()
        bump()
    }

    private fun bump() { version++ }
}

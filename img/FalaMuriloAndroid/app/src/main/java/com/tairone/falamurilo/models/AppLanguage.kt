package com.tairone.falamurilo.models

import java.util.Locale

enum class AppLanguage(val code: String, val label: String, val flag: String) {
    ENGLISH("en-US",    "English",   "🇺🇸"),
    SPANISH("es-ES",    "Español",   "🇪🇸"),
    PORTUGUESE("pt-BR", "Português", "🇧🇷");

    companion object {
        fun detected(): AppLanguage {
            val lang = Locale.getDefault().language
            return when {
                lang.startsWith("pt") -> PORTUGUESE
                lang.startsWith("es") -> SPANISH
                else -> ENGLISH
            }
        }

        fun fromCode(code: String): AppLanguage =
            entries.firstOrNull { it.code == code } ?: ENGLISH
    }
}

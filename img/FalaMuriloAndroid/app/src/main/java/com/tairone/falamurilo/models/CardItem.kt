package com.tairone.falamurilo.models

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.tairone.falamurilo.R

data class CardItem(
    val id: String,
    @DrawableRes val imageRes: Int,
    val labels: Map<String, String>,
    val color: Color,
) {
    fun label(language: AppLanguage): String =
        labels[language.code] ?: labels["en-US"] ?: id

    companion object {
        val all: List<CardItem> by lazy {
            listOf(
                CardItem("eat",      R.drawable.card_comer,    mapOf("en-US" to "Eat",      "es-ES" to "Comer",    "pt-BR" to "Comer"),    Color(0xFFFF6B6B)),
                CardItem("drink",    R.drawable.card_beber,    mapOf("en-US" to "Drink",    "es-ES" to "Beber",    "pt-BR" to "Beber"),    Color(0xFF4ECDC4)),
                CardItem("play",     R.drawable.card_brincar,  mapOf("en-US" to "Play",     "es-ES" to "Jugar",    "pt-BR" to "Brincar"),  Color(0xFF45B7D1)),
                CardItem("bathroom", R.drawable.card_banheiro, mapOf("en-US" to "Bathroom", "es-ES" to "Baño",     "pt-BR" to "Banheiro"), Color(0xFF96CEB4)),
                CardItem("dad",      R.drawable.card_pai,      mapOf("en-US" to "Dad",      "es-ES" to "Papá",     "pt-BR" to "Papai"),    Color(0xFFFFEAA7)),
                CardItem("mom",      R.drawable.card_mae,      mapOf("en-US" to "Mom",      "es-ES" to "Mamá",     "pt-BR" to "Mamãe"),    Color(0xFFDDA0DD)),
                CardItem("yes",      R.drawable.card_sim,      mapOf("en-US" to "Yes",      "es-ES" to "Sí",       "pt-BR" to "Sim"),      Color(0xFF98D8C8)),
                CardItem("no",       R.drawable.card_nao,      mapOf("en-US" to "No",       "es-ES" to "No",       "pt-BR" to "Não"),      Color(0xFFF7A8A8)),
                CardItem("help",     R.drawable.card_ajuda,    mapOf("en-US" to "Help",     "es-ES" to "Ayuda",    "pt-BR" to "Ajuda"),    Color(0xFFFFD700)),
            )
        }
    }
}

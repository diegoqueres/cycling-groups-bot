package net.diegoqueres.cyclinggroups.core.translation

import org.telegram.telegrambots.meta.api.objects.Update
import java.util.*


class LanguageResolver {

    fun getLocale(update: Update): Locale {
        val languageCode = getLanguageCode(update)
        return when {
            languageCode != null -> Locale.forLanguageTag(languageCode)
            else -> Locale.getDefault()
        }
    }

    fun getLanguageCode(update: Update): String? {
        return update.message.from.languageCode
    }

}
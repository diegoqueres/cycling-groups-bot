package net.diegoqueres.cyclinggroups.core.translation

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.abilitybots.api.util.AbilityUtils
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.*

class Translator(
    private val languageResolver: LanguageResolver = LanguageResolver()
) {
    private val logger: Logger = LoggerFactory.getLogger(Translator::class.java)

    /**
     * Messages sent by the bot that, if the translation fails, don't require the exception to be thrown forward.
     */
    fun translateMessage(update: Update?, message: String, vararg arguments: Any?): String {
        if (update == null)
            return message

        val locale = languageResolver.getLocale(update)

        return try {
            AbilityUtils.getLocalizedMessage(message, locale, *arguments)
        } catch (e: java.util.MissingResourceException) {
            logger.warn("Can't find message for: '{}'", message, e)
            message
        } catch (e: Exception) {
            logger.error("Error when try translate message: {}", message, e)
            message
        }
    }

    /**
     * Translation that affects how bot commands work. _Eg: options selected in a menu._
     * In this case, exceptions are thrown ahead.
     */
    fun translateCommandMessage(update: Update?, message: String, vararg arguments: Any?): String {
        if (update == null)
            return message

        val locale = languageResolver.getLocale(update)

        return AbilityUtils.getLocalizedMessage(message, locale, arguments)
    }

    fun translatedCommands(locales: List<Locale>, command: String): List<String> {
        return locales.map { locale ->
            AbilityUtils.getLocalizedMessage(command, locale)
        }
    }

}
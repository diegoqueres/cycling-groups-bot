package net.diegoqueres.cyclinggroups.bot.ability

import net.diegoqueres.cyclinggroups.bot.handler.ResponseHandler
import org.telegram.abilitybots.api.bot.AbilityBot
import org.telegram.abilitybots.api.objects.Ability
import org.telegram.abilitybots.api.objects.Locality
import org.telegram.abilitybots.api.objects.MessageContext
import org.telegram.abilitybots.api.objects.Privacy
import org.telegram.abilitybots.api.util.AbilityUtils
import java.util.*

abstract class AbstractBotAbility(
    val bot: AbilityBot,
    val responseHandler: ResponseHandler,
    val defaultCommand: String,
    private val defaultCommandInfo: String,
    private val defaultCommandLocality: Locality = Locality.ALL,
    private val defaultCommandPrivacy: Privacy = Privacy.PUBLIC
) {

    /**
     * Register commands to all available bot languages.
     */
    fun defaultAbility(): Ability =
        defaultAbility(Locale.ENGLISH, defaultCommandLocality, defaultCommandPrivacy, ::defaultAction)
    fun ptBrDefaultAbility(): Ability =
        defaultAbility(Locale("pt", "BR"), defaultCommandLocality, defaultCommandPrivacy, ::defaultAction)

    private fun defaultAbility(locale: Locale, locality: Locality, privacy: Privacy, action: (MessageContext) -> Unit): Ability {
        return Ability.builder()
            .name(AbilityUtils.getLocalizedMessage(defaultCommand, locale))
            .info(AbilityUtils.getLocalizedMessage(defaultCommandInfo, locale))
            .locality(locality)
            .privacy(privacy)
            .action(action)
            .build()
    }

    abstract fun defaultAction(ctx: MessageContext)

}
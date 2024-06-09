package net.diegoqueres.cyclinggroups.bot.ability

import net.diegoqueres.cyclinggroups.bot.handler.ResponseHandler
import org.telegram.abilitybots.api.bot.AbilityBot
import org.telegram.abilitybots.api.objects.MessageContext
import org.telegram.abilitybots.api.util.AbilityExtension

class BotAbilityStart(
    bot: AbilityBot,
    responseHandler: ResponseHandler
) : AbilityExtension,
    AbstractBotAbility(bot, responseHandler, "ability.start.command", "ability.start.info") {

    override fun defaultAction(ctx: MessageContext) {
        responseHandler.sendMessage("ability.start.welcome", ctx.update(), false)
    }

}
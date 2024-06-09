package net.diegoqueres.cyclinggroups.bot.ability

import net.diegoqueres.cyclinggroups.bot.handler.ResponseHandler
import org.telegram.abilitybots.api.bot.AbilityBot
import org.telegram.abilitybots.api.objects.MessageContext
import org.telegram.abilitybots.api.util.AbilityExtension

class BotAbilityHelp(
    bot: AbilityBot,
    responseHandler: ResponseHandler
) : AbilityExtension, AbstractBotAbility(bot, responseHandler, "ability.help.command", "ability.help.info") {

    override fun defaultAction(ctx: MessageContext) {
        responseHandler.sendMessage("ability.help.message", ctx.update(), false)
    }

}
package net.diegoqueres.cyclinggroups.bot

import net.diegoqueres.cyclinggroups.bot.ability.BotAbilityFind
import net.diegoqueres.cyclinggroups.bot.ability.BotAbilityHelp
import net.diegoqueres.cyclinggroups.bot.ability.BotAbilityRegister
import net.diegoqueres.cyclinggroups.bot.ability.BotAbilityStart
import net.diegoqueres.cyclinggroups.bot.handler.ResponseHandler
import net.diegoqueres.cyclinggroups.core.properties.AppProperties
import net.diegoqueres.cyclinggroups.core.properties.TelegramBotProperties
import net.diegoqueres.cyclinggroups.domain.service.CyclingGroupService
import org.springframework.stereotype.Component
import org.telegram.abilitybots.api.bot.AbilityBot

@Component
final class CyclingGroupBot(
    private val telegramBotProperties: TelegramBotProperties,
    private val responseHandler: ResponseHandler,
    cyclingGroupService: CyclingGroupService,
    appProperties: AppProperties
) : AbilityBot(telegramBotProperties.token, telegramBotProperties.username) {

    init {
        this.responseHandler.initialize(this)

        addExtension(BotAbilityStart(this, responseHandler))
        addExtension(BotAbilityHelp(this, responseHandler))
        addExtension(BotAbilityFind(this, responseHandler, appProperties, cyclingGroupService))
        addExtension(BotAbilityRegister(this, responseHandler, appProperties, cyclingGroupService))
    }

    override fun creatorId(): Long {
        return telegramBotProperties.creatorId
    }

}
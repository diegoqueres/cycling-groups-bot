package net.diegoqueres.cyclinggroups.bot.ability

import net.diegoqueres.cyclinggroups.bot.constants.UserState
import net.diegoqueres.cyclinggroups.bot.constants.UserState.Register
import net.diegoqueres.cyclinggroups.bot.handler.AbilityExceptionHandler
import net.diegoqueres.cyclinggroups.bot.handler.DbContextHandler
import net.diegoqueres.cyclinggroups.bot.handler.ResponseHandler
import net.diegoqueres.cyclinggroups.bot.util.KeyboardFactory.Companion.replyKeyboardRemove
import net.diegoqueres.cyclinggroups.bot.util.PredicateUtils.Companion.hasMessageWith
import net.diegoqueres.cyclinggroups.bot.util.PredicateUtils.Companion.hasMessageWithLocation
import net.diegoqueres.cyclinggroups.bot.util.PredicateUtils.Companion.hasMessageWithTranslatedCommand
import net.diegoqueres.cyclinggroups.bot.util.PredicateUtils.Companion.isInExpectedStates
import net.diegoqueres.cyclinggroups.bot.util.PredicateUtils.Companion.isMessageNotEmpty
import net.diegoqueres.cyclinggroups.bot.util.PredicateUtils.Companion.isReplyToBot
import net.diegoqueres.cyclinggroups.bot.util.PredicateUtils.Companion.isReplyToLastMessage
import net.diegoqueres.cyclinggroups.core.properties.AppProperties
import net.diegoqueres.cyclinggroups.core.translation.Translator
import net.diegoqueres.cyclinggroups.domain.model.CyclingGroup
import net.diegoqueres.cyclinggroups.domain.service.CyclingGroupService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.telegram.abilitybots.api.bot.AbilityBot
import org.telegram.abilitybots.api.bot.BaseAbilityBot
import org.telegram.abilitybots.api.objects.Flag
import org.telegram.abilitybots.api.objects.MessageContext
import org.telegram.abilitybots.api.objects.Reply
import org.telegram.abilitybots.api.objects.ReplyFlow
import org.telegram.abilitybots.api.util.AbilityExtension
import org.telegram.abilitybots.api.util.AbilityUtils
import org.telegram.telegrambots.meta.api.objects.Update


class BotAbilityRegister(
    bot: AbilityBot,
    responseHandler: ResponseHandler,
    private val appProperties: AppProperties,
    private val cyclingGroupService: CyclingGroupService
) : AbilityExtension,
    AbstractBotAbility(bot, responseHandler, "ability.register.command", "ability.register.info") {
    private val logger: Logger = LoggerFactory.getLogger(BotAbilityRegister::class.java)
    private val dbContextHandler: DbContextHandler = DbContextHandler(bot.db())
    private val translator: Translator = Translator()
    private val abilityExceptionHandler: AbilityExceptionHandler = AbilityExceptionHandler(logger, responseHandler)

    override fun defaultAction(ctx: MessageContext) {}

    fun start(): ReplyFlow {
        return ReplyFlow.builder(bot.db())
            .onlyIf(Flag.TEXT)
            .onlyIf(hasMessageWithTranslatedCommand(defaultCommand, appProperties.supportedLocales()))
            .action { _: BaseAbilityBot, upd: Update ->
                dbContextHandler.initMaps(UserState.MAP_USER_STATE, UserState.MAP_USER_MESSAGE, Register.MAP_CYCLING_GROUP)
                responseHandler.sendMessage("ability.register.start", upd)
            }
            .next(askForCyclingGroupName())
            .build()
    }

    private fun askForCyclingGroupName(): ReplyFlow {
        return ReplyFlow.builder(bot.db())
            .onlyIf(Flag.TEXT)
            .onlyIf(isMessageNotEmpty())
            .action { _: BaseAbilityBot, upd: Update ->
                simpleTryCatch("create", upd) {
                    val chatId = AbilityUtils.getChatId(upd)
                    dbContextHandler.putToMap(UserState.MAP_USER_STATE, chatId, Register.ASK_GROUP_NAME)
                    responseHandler.sendMessageAndForceReply("ability.register.ask-cycling-group-name", upd)
                        .ifPresent { dbContextHandler.putToMap(UserState.MAP_USER_MESSAGE, chatId, it.messageId) }
                }
            }
            .next(askForCyclingGroupDescription())
            .build()
    }

    private fun askForCyclingGroupDescription(): ReplyFlow {
        return ReplyFlow.builder(bot.db())
            .onlyIf(Flag.TEXT)
            .onlyIf(Flag.REPLY).onlyIf(isReplyToBot(bot))
            .onlyIf(isReplyToLastMessage(dbContextHandler))
            .onlyIf(isInExpectedStates(dbContextHandler, UserState.MAP_USER_STATE, Register.ASK_GROUP_NAME))
            .action { _: BaseAbilityBot, upd: Update ->
                simpleTryCatch("create", upd) {
                    val chatId = AbilityUtils.getChatId(upd)

                    val cyclingGroupMap = CyclingGroup(name = upd.message.text)
                    dbContextHandler.putToMap(Register.MAP_CYCLING_GROUP, chatId, cyclingGroupMap)

                    dbContextHandler.putToMap(UserState.MAP_USER_STATE, chatId, Register.ASK_GROUP_DESCRIPTION)
                    responseHandler.sendMessageAndForceReply("ability.register.ask-cycling-group-description", upd)
                        .ifPresent { dbContextHandler.putToMap(UserState.MAP_USER_MESSAGE, chatId, it.messageId) }
                }
            }
            .next(askForCyclingGroupSocialProfile())
            .build()
    }

    private fun askForCyclingGroupSocialProfile(): ReplyFlow {
        return ReplyFlow.builder(bot.db())
            .onlyIf(Flag.TEXT)
            .onlyIf(Flag.REPLY).onlyIf(isReplyToBot(bot))
            .onlyIf(isReplyToLastMessage(dbContextHandler))
            .onlyIf(isInExpectedStates(dbContextHandler, UserState.MAP_USER_STATE, Register.ASK_GROUP_DESCRIPTION))
            .action { _: BaseAbilityBot, upd: Update ->
                simpleTryCatch("create", upd) {
                    val chatId = AbilityUtils.getChatId(upd)

                    val cyclingGroup = dbContextHandler.getFromMap(Register.MAP_CYCLING_GROUP, chatId) as? CyclingGroup
                        ?: throw Exception("Fail to recover cycling group data")
                    cyclingGroup.description = upd.message.text
                    dbContextHandler.putToMap(Register.MAP_CYCLING_GROUP, chatId, cyclingGroup)

                    dbContextHandler.putToMap(UserState.MAP_USER_STATE, chatId, Register.ASK_GROUP_SOCIAL_PROFILE)
                    responseHandler.sendMessageAndForceReply("ability.register.ask-cycling-group-social", upd)
                        .ifPresent { dbContextHandler.putToMap(UserState.MAP_USER_MESSAGE, chatId, it.messageId) }
                }
            }
            .next(askForCyclingGroupPosition())
            .build()
    }

    private fun askForCyclingGroupPosition(): ReplyFlow {
        return ReplyFlow.builder(bot.db())
            .onlyIf(Flag.TEXT)
            .onlyIf(Flag.REPLY).onlyIf(isReplyToBot(bot))
            .onlyIf(isReplyToLastMessage(dbContextHandler))
            .onlyIf(isInExpectedStates(dbContextHandler, UserState.MAP_USER_STATE, Register.ASK_GROUP_SOCIAL_PROFILE))
            .action { _: BaseAbilityBot, upd: Update ->
                simpleTryCatch("create", upd) {
                    val chatId = AbilityUtils.getChatId(upd)

                    val cyclingGroup = dbContextHandler.getFromMap(Register.MAP_CYCLING_GROUP, chatId) as? CyclingGroup
                        ?: throw Exception("Fail to recover cycling group data")
                    cyclingGroup.socialProfileUrl = upd.message.text
                    dbContextHandler.putToMap(Register.MAP_CYCLING_GROUP, chatId, cyclingGroup)

                    dbContextHandler.putToMap(UserState.MAP_USER_STATE, chatId, Register.ASK_GROUP_POSITION)
                    responseHandler.sendMessageWithRequestLocationButton(
                        message = "ability.register.ask-share-location",
                        update = upd,
                        mainButtonText = "ability.register.button.share-location",
                        cancelButtonText = "ability.register.button.cancel"
                    )
                }
            }
            .next(askForCyclingGroupConfirmation())
            .next(cancel())
            .build()
    }

    private fun askForCyclingGroupConfirmation(): ReplyFlow {
        return ReplyFlow.builder(bot.db())
            .onlyIf(Flag.LOCATION)
            .onlyIf(hasMessageWithLocation())
            .onlyIf(isInExpectedStates(dbContextHandler, UserState.MAP_USER_STATE, Register.ASK_GROUP_POSITION))
            .action { _: BaseAbilityBot, upd: Update ->
                simpleTryCatch("create", upd) {
                    val chatId = AbilityUtils.getChatId(upd)
                    dbContextHandler.putToMap(UserState.MAP_USER_STATE, chatId, Register.ASK_GROUP_CONFIRMATION)

                    val cyclingGroup = dbContextHandler.getFromMap(Register.MAP_CYCLING_GROUP, chatId) as? CyclingGroup
                        ?: throw Exception("Fail to recover cycling group data")
                    cyclingGroup.position = GeoJsonPoint(upd.message.location.latitude, upd.message.location.longitude)
                    dbContextHandler.putToMap(Register.MAP_CYCLING_GROUP, chatId, cyclingGroup)

                    val productResume = translator.translateMessage(
                        upd, "ability.register.new-cycling-group-resume",
                        cyclingGroup.name, cyclingGroup.description
                    )
                    responseHandler.sendMessage(productResume.trimMargin(), upd)

                    responseHandler.sendMessageAddReplyKeyboard(
                        "ability.register.ask-confirmation", upd,
                        keyboardOptions = arrayOf("ability.register.button.yes", "ability.register.button.no")
                    )
                }
            }
            .next(save())
            .next(cancel())
            .build()
    }

    private fun save(): Reply {
        return Reply.of(
            { _: BaseAbilityBot, upd: Update ->
                try {
                    val chatId = AbilityUtils.getChatId(upd)
                    responseHandler.executeTypingAction(chatId)

                    val cyclingGroup = dbContextHandler.getFromMap(Register.MAP_CYCLING_GROUP, chatId) as? CyclingGroup
                        ?: throw Exception("Fail to recover cycling group data")
                    val savedCyclingGroup = cyclingGroupService.save(cyclingGroup)

                    logger.info("Cycling group was saved successfully: #{} {}", savedCyclingGroup.id, cyclingGroup.name)

                    val userMessage: String =
                        translator.translateMessage(upd, "ability.register.cycling-group-created", cyclingGroup.name)
                    responseHandler.sendMessageWithReplyMarkup(userMessage, chatId, typingAction = false, replyKeyboardRemove())
                } catch (e: Exception) {
                    logger.error("Fail to save Cycling Group", e)
                    responseHandler.reportError("ability.register.fail", upd)
                } finally {
                    dbContextHandler.removeFromMap(AbilityUtils.getChatId(upd),
                        UserState.MAP_USER_STATE, UserState.MAP_USER_MESSAGE, Register.MAP_CYCLING_GROUP)
                }
            },
            Flag.TEXT,
            hasMessageWith("ability.register.button.yes"),
            isInExpectedStates(dbContextHandler, UserState.MAP_USER_STATE, Register.ASK_GROUP_CONFIRMATION)
        )
    }

    private fun cancel(): Reply {
        return Reply.of(
            { _: BaseAbilityBot, upd: Update ->
                simpleTryCatch("cancel", upd) {
                    val chatId = AbilityUtils.getChatId(upd)
                    dbContextHandler.removeFromMap(chatId,
                        UserState.MAP_USER_STATE, UserState.MAP_USER_MESSAGE, Register.MAP_CYCLING_GROUP)
                    responseHandler.sendMessageRemoveReplyKeyboard("ability.register.cancel-confirmation", upd)
                }
            },
            Flag.TEXT,
            hasMessageWith("ability.register.button.cancel", "ability.register.button.no"),
            isInExpectedStates(dbContextHandler, UserState.MAP_USER_STATE, Register.ASK_GROUP_POSITION, Register.ASK_GROUP_CONFIRMATION)
        )
    }

    private fun simpleTryCatch(operation: String, upd: Update, code: () -> Unit) {
        abilityExceptionHandler.simpleTryCatch(operation, upd, code)
    }

}
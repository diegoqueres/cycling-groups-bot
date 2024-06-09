package net.diegoqueres.cyclinggroups.bot.ability

import net.diegoqueres.cyclinggroups.bot.constants.UserState
import net.diegoqueres.cyclinggroups.bot.constants.UserState.Find
import net.diegoqueres.cyclinggroups.bot.exception.UserException
import net.diegoqueres.cyclinggroups.bot.handler.DbContextHandler
import net.diegoqueres.cyclinggroups.bot.handler.ResponseHandler
import net.diegoqueres.cyclinggroups.bot.util.KeyboardFactory.Companion.replyKeyboardRemove
import net.diegoqueres.cyclinggroups.bot.util.PredicateUtils.Companion.hasMessageWith
import net.diegoqueres.cyclinggroups.bot.util.PredicateUtils.Companion.hasMessageWithLocation
import net.diegoqueres.cyclinggroups.bot.util.PredicateUtils.Companion.hasMessageWithTranslatedCommand
import net.diegoqueres.cyclinggroups.bot.util.PredicateUtils.Companion.isInExpectedStates
import net.diegoqueres.cyclinggroups.bot.util.PredicateUtils.Companion.isReplyToLastMessage
import net.diegoqueres.cyclinggroups.core.properties.AppProperties
import net.diegoqueres.cyclinggroups.core.translation.Translator
import net.diegoqueres.cyclinggroups.domain.model.CyclingGroup
import net.diegoqueres.cyclinggroups.domain.service.CyclingGroupService
import net.diegoqueres.cyclinggroups.infrastructure.adapter.openstreetmap.OpenStreetMapClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.abilitybots.api.bot.AbilityBot
import org.telegram.abilitybots.api.bot.BaseAbilityBot
import org.telegram.abilitybots.api.objects.Flag
import org.telegram.abilitybots.api.objects.MessageContext
import org.telegram.abilitybots.api.objects.Reply
import org.telegram.abilitybots.api.objects.ReplyFlow
import org.telegram.abilitybots.api.util.AbilityExtension
import org.telegram.abilitybots.api.util.AbilityUtils
import org.telegram.telegrambots.meta.api.objects.Update

class BotAbilityFind(
    bot: AbilityBot,
    responseHandler: ResponseHandler,
    private val appProperties: AppProperties,
    private val cyclingGroupService: CyclingGroupService
) : AbilityExtension,
    AbstractBotAbility(bot, responseHandler, "ability.find.command", "ability.find.info") {
    private val logger: Logger = LoggerFactory.getLogger(BotAbilityFind::class.java)
    private val dbContextHandler: DbContextHandler = DbContextHandler(bot.db())
    private val translator: Translator = Translator()

    override fun defaultAction(ctx: MessageContext) {}

    fun start(): ReplyFlow {
        return ReplyFlow.builder(bot.db())
            .onlyIf(Flag.TEXT)
            .onlyIf(hasMessageWithTranslatedCommand(defaultCommand, appProperties.supportedLocales()))
            .action { _: BaseAbilityBot, upd: Update ->
                try {
                    val chatId = AbilityUtils.getChatId(upd)
                    dbContextHandler.initMaps(UserState.MAP_USER_STATE)

                    dbContextHandler.putToMap(UserState.MAP_USER_STATE, chatId, Find.ASK_USER_POSITION)
                    responseHandler.sendMessageWithRequestLocationButton(
                        update = upd,
                        message = "ability.find.ask-position",
                        mainButtonText = "ability.find.button.share-location",
                        cancelButtonText = "ability.find.button.cancel",
                        othersButtonText = arrayOf("ability.find.button.write-city-name"),
                    )
                } catch (e: Exception) {
                    logger.error("Fail to find cycling groups", e)
                    responseHandler.reportError("ability.find.fail-search", upd)
                }
            }
            .next(findCyclingGroupsByLocation())
            .next(askForCyclingGroupCity())
            .next(cancel())
            .build()
    }

    private fun findCyclingGroupsByLocation(): Reply {
        return Reply.of(
            { _: BaseAbilityBot, upd: Update ->
                try {
                    val chatId = AbilityUtils.getChatId(upd)
                    responseHandler.executeTypingAction(chatId)

                    val latitude = upd.message.location.latitude
                    val longitude = upd.message.location.longitude
                    val results = cyclingGroupService.findByPositionNear(latitude, longitude)

                    val resultsMessage = buildResultsMessage(upd, results)
                    responseHandler.sendMessageWithReplyMarkup(resultsMessage, chatId, typingAction = false, replyKeyboardRemove())

                    logger.info("Search for cycling groups on latitude {} longitude {}: {} results", latitude, longitude, results.size)
                } catch (e: Exception) {
                    logger.error("Fail to search for cycling groups", e)
                    responseHandler.reportError("ability.find.fail-search", upd)
                } finally {
                    dbContextHandler.removeFromMap(AbilityUtils.getChatId(upd), UserState.MAP_USER_STATE)
                }
            },
            Flag.LOCATION,
            hasMessageWithLocation(),
            isInExpectedStates(dbContextHandler, UserState.MAP_USER_STATE, Find.ASK_USER_POSITION)
        )
    }

    private fun askForCyclingGroupCity(): ReplyFlow {
        return ReplyFlow.builder(bot.db())
            .onlyIf(Flag.TEXT)
            .onlyIf(hasMessageWith("ability.find.button.write-city-name"))
            .onlyIf(isInExpectedStates(dbContextHandler, UserState.MAP_USER_STATE, Find.ASK_USER_POSITION))
            .action { _: BaseAbilityBot, upd: Update ->
                try {
                    val chatId = AbilityUtils.getChatId(upd)
                    dbContextHandler.putToMap(UserState.MAP_USER_STATE, chatId, Find.ASK_USER_CITY_NAME)
                    responseHandler.sendMessageAndForceReply("ability.find.ask-city-name", upd)
                        .ifPresent { dbContextHandler.putToMap(UserState.MAP_USER_MESSAGE, chatId, it.messageId) }
                } catch (e: Exception) {
                    logger.error("Fail to find cycling groups", e)
                    responseHandler.reportError("ability.find.fail-search", upd)
                }
            }
            .next(findCyclingGroupsByAddress())
            .next(cancel())
            .build()
    }

    private fun findCyclingGroupsByAddress(): Reply {
        return Reply.of(
            { _: BaseAbilityBot, upd: Update ->
                try {
                    val chatId = AbilityUtils.getChatId(upd)
                    responseHandler.executeTypingAction(chatId)

                    val address = upd.message.text

                    val mapClient = OpenStreetMapClient()
                    val coordinates = mapClient.getCoordinatesFromCity(address)
                        ?: throw UserException("City was not found", address)

                    val (latitude: Double, longitude: Double) = coordinates
                    val results = cyclingGroupService.findByPositionNear(latitude, longitude)

                    val resultsMessage = buildResultsMessage(upd, results)
                    responseHandler.sendMessageWithReplyMarkup(resultsMessage, chatId, typingAction = false, replyKeyboardRemove())

                    logger.info("Search for cycling groups on latitude {} longitude {}: {} results", latitude, longitude, results.size)
                } catch (e: UserException) {
                    logger.warn(e.message)
                    responseHandler.reportError("ability.find.city-not-found", upd)
                } catch (e: Exception) {
                    logger.error("Fail to search for cycling groups", e)
                    responseHandler.reportError("ability.find.fail-search", upd)
                } finally {
                    dbContextHandler.removeFromMap(AbilityUtils.getChatId(upd), UserState.MAP_USER_STATE)
                }
            },
            Flag.REPLY,
            isReplyToLastMessage(dbContextHandler),
            isInExpectedStates(dbContextHandler, UserState.MAP_USER_STATE, Find.ASK_USER_CITY_NAME)
        )
    }

    private fun cancel(): Reply {
        return Reply.of(
            { _: BaseAbilityBot, upd: Update ->
                try {
                    val chatId = AbilityUtils.getChatId(upd)
                    dbContextHandler.removeFromMap(chatId, UserState.MAP_USER_STATE)
                    responseHandler.sendMessageRemoveReplyKeyboard("Canceled successfully", upd)
                } catch (e: Exception) {
                    logger.error("Fail to cancel find Cycling Groups", e)
                    responseHandler.reportError("generic.fail-operation", upd)
                }
            },
            Flag.TEXT,
            hasMessageWith("Cancel"),
            isInExpectedStates(dbContextHandler, UserState.MAP_USER_STATE, Find.ASK_USER_POSITION, Find.ASK_USER_CITY_NAME)
        )
    }

    private fun buildResultsMessage(upd: Update, cyclingGroups: List<CyclingGroup>): String {
        if (cyclingGroups.isEmpty())
            return translator.translateMessage(upd, "ability.find.no-results-found")

        val message = StringBuilder()
        val mapClient = OpenStreetMapClient()

        message.append(translator.translateMessage(upd, "ability.find.results.title"))
        message.appendLine().appendLine()

        for (group in cyclingGroups) {
            val groupCity = mapClient.getCityFromCoordinates(group.position!!.x, group.position!!.y) ?: "N/A"

            message.appendLine(
                translator.translateMessage(upd, "ability.find.result.record",
                    group.name ?: "N/A",
                    group.description ?: "N/A",
                    group.socialProfileUrl ?: "N/A",
                    groupCity
                )
            )
            message.appendLine()
        }

        return message.toString()
    }

}
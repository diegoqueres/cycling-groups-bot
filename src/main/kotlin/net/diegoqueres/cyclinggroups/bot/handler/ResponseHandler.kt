package net.diegoqueres.cyclinggroups.bot.handler

import net.diegoqueres.cyclinggroups.bot.CyclingGroupBot
import net.diegoqueres.cyclinggroups.bot.util.KeyboardFactory.Companion.replyKeyboard
import net.diegoqueres.cyclinggroups.bot.util.KeyboardFactory.Companion.replyKeyboardWithRequestLocationButton
import net.diegoqueres.cyclinggroups.core.translation.AutoTranslate
import org.springframework.stereotype.Component
import org.telegram.abilitybots.api.sender.SilentSender
import org.telegram.abilitybots.api.util.AbilityUtils
import org.telegram.telegrambots.meta.api.methods.ActionType
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction
import org.telegram.telegrambots.meta.api.methods.send.SendLocation
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import java.util.*

/**
 * Help to abilities send bot responses to user commands.
 */
@Component
class ResponseHandler {
    private lateinit var silentSender: SilentSender

    fun initialize(bot: CyclingGroupBot) {
        this.silentSender = bot.silent()
    }

    @AutoTranslate(translateArgs = ["message"])
    fun sendMessage(message: String, update: Update, typingAction: Boolean = true) {
        val chatId = AbilityUtils.getChatId(update)

        if (typingAction)
            executeTypingAction(chatId)

        sendMarkdownMessage(message, chatId)
    }

    @AutoTranslate(translateArgs = ["message"])
    fun reportError(message: String, update: Update) {
        val chatId = AbilityUtils.getChatId(update)
        sendMessageWithReplyMarkup(message, chatId, false, ReplyKeyboardRemove(true))
    }

    @AutoTranslate(translateArgs = ["message"])
    fun sendMessageAndForceReply(message: String, update: Update, typingAction: Boolean = true): Optional<Message> {
        val chatId = AbilityUtils.getChatId(update)

        if (typingAction)
            executeTypingAction(chatId)

        return silentSender.forceReply(message, chatId)
    }

    @AutoTranslate(translateArgs = ["message", "keyboardOptions"])
    fun sendMessageAddReplyKeyboard(
        message: String,
        update: Update,
        typingAction: Boolean = true,
        keyboardOptions: Array<String>,
        vararg arguments: Any?
    ): Optional<Message> {
        val chatId = AbilityUtils.getChatId(update)
        return sendMessageWithReplyMarkup(message, chatId, typingAction, replyKeyboard(*keyboardOptions))
    }

    @AutoTranslate(translateArgs = ["message"])
    fun sendMessageRemoveReplyKeyboard(message: String, update: Update, typingAction: Boolean = true, vararg arguments: Any?): Optional<Message> {
        val chatId = AbilityUtils.getChatId(update)
        return sendMessageWithReplyMarkup(message, chatId, typingAction, ReplyKeyboardRemove(true))
    }

    @AutoTranslate(translateArgs = ["message", "mainButtonText", "cancelButtonText", "othersButtonText"])
    fun sendMessageWithRequestLocationButton(
        update: Update,
        message: String,
        mainButtonText: String,
        cancelButtonText: String? = null,
        othersButtonText: Array<String> = emptyArray(),
        typingAction: Boolean = true
    ): Optional<Message> {
        val chatId = AbilityUtils.getChatId(update)

        if (typingAction)
            executeTypingAction(chatId)

        val sendMessage = SendMessage.builder()
            .text(message)
            .chatId(chatId)
            .replyMarkup(replyKeyboardWithRequestLocationButton(mainButtonText, cancelButtonText, *othersButtonText))
            .parseMode(ParseMode.MARKDOWN)
            .build()

        return silentSender.execute(sendMessage)
    }

    fun sendLocation(chatId: Long, latitude: Double, longitude: Double, replyToMessageId: Int? = null) {
        silentSender.execute(
            SendLocation.builder()
                .chatId(chatId)
                .latitude(latitude)
                .longitude(longitude)
                .replyToMessageId(replyToMessageId)
                .build()
        )
    }

    fun sendMessageWithReplyMarkup(
        message: String,
        chatId: Long,
        typingAction: Boolean = true,
        replyMarkup: ReplyKeyboard
    ): Optional<Message> {
        if (typingAction)
            executeTypingAction(chatId)

        val sendMessage = SendMessage.builder()
            .chatId(chatId)
            .text(message)
            .replyMarkup(replyMarkup)
            .parseMode(ParseMode.MARKDOWN)
            .build()

        return silentSender.execute(sendMessage)
    }

    fun executeTypingAction(chatId: Long) {
        silentSender.execute(
            SendChatAction.builder()
                .chatId(chatId)
                .action(ActionType.TYPING.toString())
                .build()
        )
    }

    private fun sendMarkdownMessage(message: String, chatId: Long) {
        silentSender.execute(SendMessage.builder()
            .text(message)
            .chatId(chatId)
            .parseMode(ParseMode.MARKDOWN)
            .build())
    }

}
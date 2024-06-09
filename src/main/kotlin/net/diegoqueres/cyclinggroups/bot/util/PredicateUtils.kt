package net.diegoqueres.cyclinggroups.bot.util

import net.diegoqueres.cyclinggroups.bot.constants.UserState
import net.diegoqueres.cyclinggroups.bot.handler.DbContextHandler
import net.diegoqueres.cyclinggroups.core.translation.Translator
import org.telegram.abilitybots.api.bot.BaseAbilityBot
import org.telegram.abilitybots.api.util.AbilityUtils
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.*
import java.util.function.Predicate

class PredicateUtils {
    companion object {
        private val translator = Translator()

        fun hasMessageWithCommand(command: String): Predicate<Update> {
            return Predicate<Update> { upd ->
                upd.message.text.equals("/$command", true)
            }
        }

        fun hasMessageWithTranslatedCommand(command: String, supportedLocales: List<Locale>): Predicate<Update> {
            return Predicate<Update> { upd ->
                val translatedCommands = translator.translatedCommands(supportedLocales, command)
                translatedCommands.any { translatedCommand ->
                    upd.message.text.equals("/$translatedCommand", true)
                }
            }
        }

        fun hasMessageWith(vararg msgs: String): Predicate<Update> {
            return Predicate<Update> { upd ->
                msgs.any { msg ->
                    val translatedMessage = translator.translateCommandMessage(upd, msg)
                    upd.message.text.equals(translatedMessage, true)
                }
            }
        }

        fun hasMessageWithLocation(): Predicate<Update> {
            return Predicate<Update> { upd ->
                upd.message.hasLocation()
            }
        }

        fun isMessageNotEmpty(): Predicate<Update> {
            return Predicate<Update> { upd ->
                upd.hasMessage() && upd.message.text.isNotEmpty()
            }
        }

        fun isReplyToMessage(message: String): Predicate<Update> {
            return Predicate { upd: Update ->
                val reply = upd.message.replyToMessage
                reply.hasText() && reply.text.equals(message, ignoreCase = true)
            }
        }

        fun isReplyToLastMessage(dbContextHandler: DbContextHandler): Predicate<Update> {
            return Predicate { upd: Update ->
                val reply = upd.message.replyToMessage
                val lastMessageId: Int? = dbContextHandler.getFromMap(UserState.MAP_USER_MESSAGE, AbilityUtils.getChatId(upd)) as Int?
                when (lastMessageId) {
                    null -> false
                    else -> reply.messageId == lastMessageId
                }
            }
        }

        fun isReplyToBot(bot: BaseAbilityBot): Predicate<Update> {
            return Predicate<Update> { upd: Update ->
                upd.message.replyToMessage.from.userName.equals(bot.botUsername, ignoreCase = true)
            }
        }

        fun isInExpectedStates(dbContextHandler: DbContextHandler, idUserStateMap: String, vararg expectedStates: Any): Predicate<Update> {
            return Predicate<Update> { upd: Update ->
                expectedStates.contains(
                    dbContextHandler.getFromMap(idUserStateMap, AbilityUtils.getChatId(upd))
                )
            }
        }
    }
}
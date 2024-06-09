package net.diegoqueres.cyclinggroups.bot.handler

import org.slf4j.Logger
import org.telegram.telegrambots.meta.api.objects.Update

/**
 * Helps to create clean 'try..catch' for abilities commands.
 */
class AbilityExceptionHandler(
    private val logger: Logger,
    private val responseHandler: ResponseHandler
) {

    fun simpleTryCatch(operation: String, upd: Update, code: () -> Unit) {
        try {
            code()
        } catch (e: Exception) {
            logger.error("Fail to $operation Cycling Group", e)
            responseHandler.reportError("generic.fail-operation", upd)
        }
    }

    fun simpleTryCatch(upd: Update, code: () -> Unit) {
        try {
            code()
        } catch (e: Exception) {
            if (upd.message.isCommand)
                logger.error("Fail to complete ability operation: {}", upd.message.text, e)
            else
                logger.error("Fail to complete ability operation", e)

            responseHandler.reportError("generic.fail-operation", upd)
        }
    }

}
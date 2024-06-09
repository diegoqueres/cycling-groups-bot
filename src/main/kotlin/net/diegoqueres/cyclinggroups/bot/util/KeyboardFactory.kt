package net.diegoqueres.cyclinggroups.bot.util

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

class KeyboardFactory {
    companion object {

        fun replyKeyboard(vararg options: String): ReplyKeyboard {
            val row = KeyboardRow()
            options.forEach(row::add)

            return ReplyKeyboardMarkup.builder()
                .keyboardRow(row)
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .build()
        }

        fun replyKeyboardWithRequestLocationButton(mainButtonText: String, cancelText: String?, vararg othersButtonText: String?): ReplyKeyboard {
            val rowRequest = KeyboardRow()
            rowRequest.add(KeyboardButton.builder().text(mainButtonText).requestLocation(true).build())
            othersButtonText.filterNotNull().filter(String::isNotEmpty).forEach(rowRequest::add)

            val replyKeyboardMarkup = ReplyKeyboardMarkup.builder()
                .oneTimeKeyboard(true)
                .resizeKeyboard(true)
                .selective(true)
                .keyboardRow(rowRequest)

            if (cancelText != null) {
                val rowCancel = KeyboardRow()
                rowCancel.add(KeyboardButton.builder().text(cancelText).build())
                replyKeyboardMarkup.keyboardRow(rowCancel)
            }

            return replyKeyboardMarkup.build()
        }

        fun replyKeyboardRemove() = ReplyKeyboardRemove(true)

    }
}
package net.diegoqueres.cyclinggroups.bot.handler

import org.telegram.abilitybots.api.db.DBContext

/**
 * Helps to manage DBContext of 'telegram-bots' library.
 */
class DbContextHandler(private val db: DBContext) {

    fun initMaps(vararg identifier: String) {
        identifier.forEach { getMap(it) }
    }

    fun getMap(identifier: String): MutableMap<Long, Any> = db.getMap(identifier)

    fun putToMap(identifier: String, chatId: Long, value: Any) {
        this.getMap(identifier)[chatId] = value
    }

    fun getFromMap(identifier: String, chatId: Long): Any? = this.getMap(identifier)[chatId]

    fun removeFromMap(chatId: Long, vararg identifier: String) = identifier.forEach { removeFromMap(chatId, it) }

    fun removeFromMap(chatId: Long, identifier: String) = this.getMap(identifier).remove(chatId)

}
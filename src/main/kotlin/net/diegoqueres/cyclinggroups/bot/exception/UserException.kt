package net.diegoqueres.cyclinggroups.bot.exception

class UserException(message: String, vararg val params: Any)
    : RuntimeException(message, null)
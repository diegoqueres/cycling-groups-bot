package net.diegoqueres.cyclinggroups.bot.constants

class UserState {
    companion object {
        const val MAP_USER_STATE = "MAP_USER_STATE"
        const val MAP_USER_MESSAGE = "MAP_USER_MESSAGE"
    }

    enum class Register {
        ASK_GROUP_NAME, ASK_GROUP_DESCRIPTION, ASK_GROUP_POSITION, ASK_GROUP_SOCIAL_PROFILE, ASK_GROUP_CONFIRMATION;

        companion object {
            const val MAP_CYCLING_GROUP = "MAP_REGISTER_CYCLING_GROUP"
        }
    }

    enum class Find {
        ASK_USER_POSITION, ASK_USER_CITY_NAME
    }

}
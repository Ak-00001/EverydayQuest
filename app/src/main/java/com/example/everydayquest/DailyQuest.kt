package com.example.everydayquest

data class DailyQuest(
    val type: String,
    val title: String,
    val target: Int,
    val xp: Int
)

object DailyQuestProvider {

    fun getTodaysQuests(): List<DailyQuest> {

        return when (
            java.util.Calendar.getInstance()
                .get(java.util.Calendar.DAY_OF_WEEK)
        ) {

            java.util.Calendar.MONDAY -> listOf(
                DailyQuest("WALK", "Walk 15 m", 15, 100),
                DailyQuest("EXPLORE", "Explore 10 m", 10, 150),
                DailyQuest("MOVE", "Move for 30 seconds", 30, 100)
            )

            java.util.Calendar.TUESDAY -> listOf(
                DailyQuest("WALK", "Walk 20 m", 20, 125),
                DailyQuest("EXPLORE", "Explore 15 m", 15, 150),
                DailyQuest("MOVE", "Move for 30 seconds", 30, 100)
            )

            java.util.Calendar.WEDNESDAY -> listOf(
                DailyQuest("WALK", "Walk 25 m", 25, 150),
                DailyQuest("EXPLORE", "Explore 10 m", 10, 125),
                DailyQuest("MOVE", "Move for 45 seconds", 45, 125)
            )

            java.util.Calendar.THURSDAY -> listOf(
                DailyQuest("WALK", "Walk 15 m", 15, 100),
                DailyQuest("EXPLORE", "Explore 20 m", 20, 175),
                DailyQuest("MOVE", "Move for 45 seconds", 45, 125)
            )

            java.util.Calendar.FRIDAY -> listOf(
                DailyQuest("WALK", "Walk 30 m", 30, 175),
                DailyQuest("EXPLORE", "Explore 15 m", 15, 150),
                DailyQuest("MOVE", "Move for 30 seconds", 30, 100)
            )

            java.util.Calendar.SATURDAY -> listOf(
                DailyQuest("WALK", "Walk 20 m", 20, 125),
                DailyQuest("EXPLORE", "Explore 25 m", 25, 200),
                DailyQuest("MOVE", "Move for 60 seconds", 60, 150)
            )

            else -> listOf(
                DailyQuest("WALK", "Walk 15 m", 15, 100),
                DailyQuest("EXPLORE", "Explore 30 m", 30, 225),
                DailyQuest("MOVE", "Move for 45 seconds", 45, 125)
            )
        }
    }
}
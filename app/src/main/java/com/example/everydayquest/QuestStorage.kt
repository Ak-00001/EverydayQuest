package com.example.everydayquest

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object QuestStorage {

    private const val PREFS_NAME = "EverydayQuestPrefs"

    private const val XP_KEY = "total_xp"
    private const val HISTORY_KEY = "history"
    private const val DAILY_BONUS_DATE_KEY = "daily_bonus_date"

    private const val DAILY_BONUS_XP = 50

    private fun getToday(): String {
        return SimpleDateFormat(
            "dd MMMM yyyy",
            Locale.getDefault()
        ).format(Date())
    }

    fun addCompletedQuest(
        context: Context,
        questName: String,
        xp: Int
    ) {

        val prefs =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        // Never allow the same quest to be completed twice today.
        if (
            isQuestCompletedToday(
                context,
                questName
            )
        ) {
            return
        }

        val currentXp =
            prefs.getInt(
                XP_KEY,
                0
            )

        val date =
            getToday()

        val oldHistory =
            prefs.getString(
                HISTORY_KEY,
                ""
            ) ?: ""

        val newEntry =
            "$questName|$xp|$date"

        val newHistory =
            if (oldHistory.isEmpty()) {

                newEntry

            } else {

                "$oldHistory\n$newEntry"
            }

        prefs.edit()
            .putInt(
                XP_KEY,
                currentXp + xp
            )
            .putString(
                HISTORY_KEY,
                newHistory
            )
            .apply()
    }

    fun getTotalXp(
        context: Context
    ): Int {

        val prefs =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        return prefs.getInt(
            XP_KEY,
            0
        )
    }

    fun isQuestCompletedToday(
        context: Context,
        questName: String
    ): Boolean {

        val prefs =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        val history =
            prefs.getString(
                HISTORY_KEY,
                ""
            ) ?: ""

        if (history.isEmpty()) {
            return false
        }

        val today =
            getToday()

        return history
            .split("\n")
            .any { entry ->

                val parts =
                    entry.split("|")

                parts.size == 3 &&
                        parts[0] == questName &&
                        parts[2] == today
            }
    }

    fun getCompletedQuestCountToday(
        context: Context,
        quests: List<DailyQuest>
    ): Int {

        return quests.count { quest ->

            isQuestCompletedToday(
                context,
                quest.title
            )
        }
    }

    fun isDailyBonusAwardedToday(
        context: Context
    ): Boolean {

        val prefs =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        val bonusDate =
            prefs.getString(
                DAILY_BONUS_DATE_KEY,
                ""
            )

        return bonusDate == getToday()
    }

    fun addDailyBonusIfEligible(
        context: Context,
        quests: List<DailyQuest>
    ): Boolean {

        if (
            isDailyBonusAwardedToday(
                context
            )
        ) {
            return false
        }

        val completedCount =
            getCompletedQuestCountToday(
                context,
                quests
            )

        if (
            completedCount !=
            quests.size
        ) {
            return false
        }

        val prefs =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        val currentXp =
            prefs.getInt(
                XP_KEY,
                0
            )

        prefs.edit()
            .putInt(
                XP_KEY,
                currentXp + DAILY_BONUS_XP
            )
            .putString(
                DAILY_BONUS_DATE_KEY,
                getToday()
            )
            .apply()

        return true
    }

    fun getDailyBonusXp(): Int {
        return DAILY_BONUS_XP
    }

    fun getHistory(
        context: Context
    ): List<HistoryItem> {

        val prefs =
            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        val historyString =
            prefs.getString(
                HISTORY_KEY,
                ""
            ) ?: ""

        if (historyString.isEmpty()) {
            return emptyList()
        }

        return historyString
            .split("\n")
            .reversed()
            .mapNotNull { entry ->

                val parts =
                    entry.split("|")

                if (parts.size == 3) {

                    HistoryItem(
                        questName = parts[0],
                        xp = parts[1].toIntOrNull() ?: 0,
                        date = parts[2]
                    )

                } else {

                    null
                }
            }
    }
}
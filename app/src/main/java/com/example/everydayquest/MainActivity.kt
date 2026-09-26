package com.example.everydayquest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var questButton1: Button
    private lateinit var questButton2: Button
    private lateinit var questButton3: Button

    private lateinit var levelText: TextView
    private lateinit var xpText: TextView
    private lateinit var xpProgressBar: ProgressBar
    private lateinit var dateText: TextView

    private lateinit var dailyProgressText: TextView
    private lateinit var dailyBonusText: TextView

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        setContentView(
            R.layout.activity_main
        )

        dateText =
            findViewById(
                R.id.dateText
            )

        levelText =
            findViewById(
                R.id.levelText
            )

        xpText =
            findViewById(
                R.id.xpText
            )

        xpProgressBar =
            findViewById(
                R.id.xpProgressBar
            )

        dailyProgressText =
            findViewById(
                R.id.dailyProgressText
            )

        dailyBonusText =
            findViewById(
                R.id.dailyBonusText
            )

        questButton1 =
            findViewById(
                R.id.questButton1
            )

        questButton2 =
            findViewById(
                R.id.questButton2
            )

        questButton3 =
            findViewById(
                R.id.questButton3
            )

        findViewById<Button>(
            R.id.historyButton
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    HistoryActivity::class.java
                )
            )
        }
    }

    override fun onResume() {

        super.onResume()

        updateDate()
        updateDailyProgress()
        updateHomeScreen()
        updateQuestButtons()
    }

    private fun updateDate() {

        val dateFormat =
            SimpleDateFormat(
                "EEEE, dd MMMM yyyy",
                Locale.getDefault()
            )

        dateText.text =
            dateFormat.format(
                Date()
            )
    }

    private fun updateHomeScreen() {

        val totalXp =
            QuestStorage.getTotalXp(
                this
            )

        val level =
            (totalXp / 1000) + 1

        val currentLevelXp =
            totalXp % 1000

        levelText.text =
            "Level $level"

        xpText.text =
            "$currentLevelXp / 1000 XP"

        xpProgressBar.max =
            1000

        xpProgressBar.progress =
            currentLevelXp
    }

    private fun updateDailyProgress() {

        val quests =
            DailyQuestProvider
                .getTodaysQuests()

        val completedCount =
            QuestStorage
                .getCompletedQuestCountToday(
                    this,
                    quests
                )

        if (
            completedCount ==
            quests.size
        ) {

            QuestStorage
                .addDailyBonusIfEligible(
                    this,
                    quests
                )
        }

        dailyProgressText.text =
            "Today's Progress: $completedCount / ${quests.size} Quests"

        val bonusAwarded =
            QuestStorage
                .isDailyBonusAwardedToday(
                    this
                )

        if (bonusAwarded) {

            dailyBonusText.text =
                "🎉 Daily Bonus: +${QuestStorage.getDailyBonusXp()} XP Earned"

        } else {

            dailyBonusText.text =
                "Daily Bonus: +${QuestStorage.getDailyBonusXp()} XP"
        }
    }

    private fun updateQuestButtons() {

        val quests =
            DailyQuestProvider
                .getTodaysQuests()

        setupQuestButton(
            questButton1,
            quests[0]
        )

        setupQuestButton(
            questButton2,
            quests[1]
        )

        setupQuestButton(
            questButton3,
            quests[2]
        )
    }

    private fun setupQuestButton(
        button: Button,
        quest: DailyQuest
    ) {

        val completed =
            QuestStorage.isQuestCompletedToday(
                this,
                quest.title
            )

        if (completed) {

            button.text =
                "${quest.title} • +${quest.xp} XP\n✓ Completed Today"

            button.isEnabled =
                false

        } else {

            button.text =
                "${quest.title} • +${quest.xp} XP"

            button.isEnabled =
                true

            button.setOnClickListener {

                openQuest(
                    quest
                )
            }
        }
    }

    private fun openQuest(
        quest: DailyQuest
    ) {

        if (
            QuestStorage.isQuestCompletedToday(
                this,
                quest.title
            )
        ) {

            Toast.makeText(
                this,
                "You already completed this quest today.",
                Toast.LENGTH_SHORT
            ).show()

            updateDailyProgress()
            updateQuestButtons()

            return
        }

        val intent =
            Intent(
                this,
                QuestDetailsActivity::class.java
            )

        intent.putExtra(
            "QUEST_TITLE",
            quest.title
        )

        intent.putExtra(
            "QUEST_XP",
            quest.xp
        )

        intent.putExtra(
            "QUEST_TYPE",
            quest.type
        )

        intent.putExtra(
            "QUEST_TARGET",
            quest.target
        )

        startActivity(
            intent
        )
    }
}
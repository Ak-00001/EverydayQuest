package com.example.everydayquest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class QuestDetailsActivity : AppCompatActivity() {

    private lateinit var startQuestButton: Button

    private var questTitle = ""
    private var questReward = 0
    private var questType = ""
    private var questTarget = 0

    private val activeQuestLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == RESULT_OK) {

                val completed =
                    result.data?.getBooleanExtra(
                        "QUEST_COMPLETED",
                        false
                    ) ?: false

                if (completed) {
                    updateButton()
                }
            }
        }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_quest_details
        )

        questTitle =
            intent.getStringExtra("QUEST_TITLE")
                ?: "Quest"

        questReward =
            intent.getIntExtra(
                "QUEST_XP",
                0
            )

        questType =
            intent.getStringExtra("QUEST_TYPE")
                ?: ""

        questTarget =
            intent.getIntExtra(
                "QUEST_TARGET",
                0
            )

        val questTitleText =
            findViewById<TextView>(
                R.id.questTitle
            )

        val questRewardText =
            findViewById<TextView>(
                R.id.questReward
            )

        startQuestButton =
            findViewById(
                R.id.startQuestButton
            )

        questTitleText.text =
            questTitle

        questRewardText.text =
            "+$questReward XP"

        updateButton()

        startQuestButton.setOnClickListener {

            if (
                QuestStorage.isQuestCompletedToday(
                    this,
                    questTitle
                )
            ) {

                Toast.makeText(
                    this,
                    "You already completed this quest today.",
                    Toast.LENGTH_SHORT
                ).show()

                updateButton()

                return@setOnClickListener
            }

            val activeIntent =
                Intent(
                    this,
                    ActiveQuestActivity::class.java
                )

            activeIntent.putExtra(
                "QUEST_TITLE",
                questTitle
            )

            activeIntent.putExtra(
                "QUEST_XP",
                questReward
            )

            activeIntent.putExtra(
                "QUEST_TYPE",
                questType
            )

            activeIntent.putExtra(
                "QUEST_TARGET",
                questTarget
            )

            activeQuestLauncher.launch(
                activeIntent
            )
        }
    }

    override fun onResume() {

        super.onResume()

        updateButton()
    }

    private fun updateButton() {

        val completed =
            QuestStorage.isQuestCompletedToday(
                this,
                questTitle
            )

        if (completed) {

            startQuestButton.text =
                "Completed Today ✓"

            startQuestButton.isEnabled =
                false

        } else {

            startQuestButton.text =
                "Start Quest"

            startQuestButton.isEnabled =
                true
        }
    }
}
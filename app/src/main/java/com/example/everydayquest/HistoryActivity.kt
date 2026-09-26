package com.example.everydayquest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_history)

        val recyclerView =
            findViewById<RecyclerView>(
                R.id.historyRecyclerView
            )

        val historyList =
            QuestStorage.getHistory(this)

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter =
            HistoryAdapter(historyList)
    }
}
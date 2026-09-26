package com.example.everydayquest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class HistoryItem(
    val questName: String,
    val xp: Int,
    val date: String
)

class HistoryAdapter(
    private val historyList: List<HistoryItem>
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val questName: TextView =
            itemView.findViewById(R.id.historyQuestName)

        val xp: TextView =
            itemView.findViewById(R.id.historyQuestXp)

        val date: TextView =
            itemView.findViewById(R.id.historyQuestDate)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)

        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: HistoryViewHolder,
        position: Int
    ) {
        val item = historyList[position]

        holder.questName.text = item.questName
        holder.xp.text = "+${item.xp} XP"
        holder.date.text = item.date
    }

    override fun getItemCount(): Int {
        return historyList.size
    }
}
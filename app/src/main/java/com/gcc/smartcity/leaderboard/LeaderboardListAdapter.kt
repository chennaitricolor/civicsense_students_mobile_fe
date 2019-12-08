package com.gcc.smartcity.leaderboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gcc.smartcity.R

class LeaderboardListAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var leaderBoardModelList: ArrayList<LeaderBoardRecyclerViewModel> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            0 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_leaderboard_winner_runner, parent, false)
                LeaderboardWinnerRunnerViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_leaderboard_participants, parent, false)
                LeaderBoardParticipantsViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return leaderBoardModelList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val holderData = leaderBoardModelList[position]

        when (holderData.viewType) {
            0 -> {
                val tempHolder = holder as LeaderboardWinnerRunnerViewHolder
                tempHolder.setValues(holderData.data as LeaderBoardWinnerRunnerModel)
            }
            1 -> {
                val tempHolder = holder as LeaderBoardParticipantsViewHolder
                tempHolder.setValues(holderData.data as LeaderBoardParticipantsModel)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return leaderBoardModelList[position].viewType
    }

    fun setData(model: ArrayList<LeaderBoardRecyclerViewModel>) {
        leaderBoardModelList = model
        notifyDataSetChanged()
    }

}
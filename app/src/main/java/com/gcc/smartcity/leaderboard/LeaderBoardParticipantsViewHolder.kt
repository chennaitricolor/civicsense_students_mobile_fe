package com.gcc.smartcity.leaderboard

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_leaderboard_participants.view.*

class LeaderBoardParticipantsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val leaderBoardUserName = view.layout_leaderboardUserName
    private val leaderBoardUserPointsEarned = view.layout_leaderboardUserPointsEarned
    private val leaderBoardUserRanking = view.layout_leaderboardUserRanking

    fun setValues(leaderBoardParticipants: LeaderBoardParticipantsModel) {
        leaderBoardUserName.text = leaderBoardParticipants.userName
        leaderBoardUserPointsEarned.text = leaderBoardParticipants.userPointsEarned
        leaderBoardUserRanking.text = leaderBoardParticipants.userRanking
    }

}
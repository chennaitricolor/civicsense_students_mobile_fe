package com.gcc.smartcity.leaderboard

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_leaderboard_participants.view.*

class LeaderBoardParticipantsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val leaderboardUserName  = view.layout_leaderboardUserName
    private val leaderboardUserPointsEarned  = view.layout_leaderboardUserPointsEarned
    private val leaderboardUserRanking  = view.layout_leaderboardUserRanking

    fun setValues(leaderBoardParticipants: LeaderBoardParticipantsModel) {
        leaderboardUserName.text = leaderBoardParticipants.userName
        leaderboardUserPointsEarned.text = leaderBoardParticipants.userPointsEarned
        leaderboardUserRanking.text = leaderBoardParticipants.userRanking
    }

}
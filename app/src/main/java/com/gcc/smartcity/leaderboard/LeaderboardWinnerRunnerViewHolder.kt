package com.gcc.smartcity.leaderboard

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_leaderboard_winner_runner.view.*

class LeaderboardWinnerRunnerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val leaderboardFirstUserName  = view.layout_winner_leaderboardFirstUserName
    private val leaderboardFirstUserPointsEarned  = view.layout_winner_leaderboardFirstUserPointsEarned
    private val leaderboardFirstUserRanking  = view.layout_winner_leaderboardFirstUserRanking
    private val leaderboardSecondUserName  = view.layout_winner_leaderboardSecondUserName
    private val leaderboardSecondUserPointsEarned  = view.layout_winner_leaderboardSecondUserPointsEarned
    private val leaderboardSecondUserRanking  = view.layout_winner_leaderboardSecondUserRanking
    private val leaderboardThirdUserName  = view.layout_winner_leaderboardThirdUserName
    private val leaderboardThirdUserPointsEarned  = view.layout_winner_leaderboardThirdUserPointsEarned
    private val leaderboardThirdUserRanking  = view.layout_winner_leaderboardThirdUserRanking

    fun setValues(leaderBoardWinnerRunner: LeaderBoardWinnerRunnerModel) {
        leaderboardFirstUserName.text = leaderBoardWinnerRunner.firstUserName
        leaderboardFirstUserPointsEarned.text = leaderBoardWinnerRunner.firstUserPointsEarned
        leaderboardFirstUserRanking.text = leaderBoardWinnerRunner.firstUserRanking
        leaderboardSecondUserName.text = leaderBoardWinnerRunner.secondUserName
        leaderboardSecondUserPointsEarned.text = leaderBoardWinnerRunner.secondUserPointsEarned
        leaderboardSecondUserRanking.text = leaderBoardWinnerRunner.secondUserRanking
        leaderboardThirdUserName.text = leaderBoardWinnerRunner.thirdUserName
        leaderboardThirdUserPointsEarned.text = leaderBoardWinnerRunner.thirdUserPointsEarned
        leaderboardThirdUserRanking.text = leaderBoardWinnerRunner.thirdUserRanking
    }

}
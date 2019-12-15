package com.gcc.smartcity.leaderboard

data class LeaderBoardWinnerRunnerModel(
    val firstUserName: String,
    val firstUserPointsEarned: String,
    val firstUserRanking: String,
    val secondUserName: String,
    val secondUserPointsEarned: String,
    val secondUserRanking: String,
    val thirdUserName: String,
    val thirdUserPointsEarned: String,
    val thirdUserRanking: String
)
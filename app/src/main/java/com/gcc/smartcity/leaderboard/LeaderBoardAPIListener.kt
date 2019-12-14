package com.gcc.smartcity.leaderboard

interface LeaderBoardAPIListener {
    fun onSuccess(leaderBoardRecyclerViewModel: ArrayList<LeaderBoardRecyclerViewModel>)
    fun onFail(message: String)
}
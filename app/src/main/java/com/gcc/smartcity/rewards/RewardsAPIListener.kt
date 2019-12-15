package com.gcc.smartcity.rewards

import com.gcc.smartcity.leaderboard.LeaderBoardRecyclerViewModel

interface RewardsAPIListener {
    fun onSuccess(rewardsRecyclerViewModel: ArrayList<RewardsRecyclerViewModel>)
    fun onFail(message: String)
}
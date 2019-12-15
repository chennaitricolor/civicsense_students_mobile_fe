package com.gcc.smartcity.rewards

interface RewardsAPIListener {
    fun onSuccess(rewardsRecyclerViewModel: ArrayList<RewardsRecyclerViewModel>)
    fun onFail(message: String)
}
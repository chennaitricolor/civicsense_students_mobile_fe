package com.gcc.smartcity.rewards

class RewardsController {

    companion object {
        private val leaderBoardAdapterData = ArrayList<RewardsRecyclerViewModel>()

        fun getAdapterData(): ArrayList<RewardsRecyclerViewModel> {
            leaderBoardAdapterData.clear()
//            for (every reward)
            val tempRewardModel = RewardsModel("image","Chennai Metro Ride", "get a free metro ride","Insufficient Balance","Collect 100 more gems to unlock", "100")
            val rewardModel = RewardsRecyclerViewModel(tempRewardModel,0)
            leaderBoardAdapterData.add(rewardModel)
            val tempRewardModel1 = RewardsModel("image","Chennai Bus Ride", "get a free metro ride","Insufficient Balance","Collect 100 more gems to unlock", "100")
            val rewardModel1 = RewardsRecyclerViewModel(tempRewardModel1,0)
            leaderBoardAdapterData.add(rewardModel1)
            val tempRewardModel2 = RewardsModel("image","Chennai Boat Ride", "get a free metro ride","Insufficient Balance","Collect 100 more gems to unlock", "100")
            val rewardModel2 = RewardsRecyclerViewModel(tempRewardModel2,0)
            leaderBoardAdapterData.add(rewardModel2)
            return leaderBoardAdapterData
        }
    }
}
package com.gcc.smartcity.rewards

import android.content.Context
import bolts.Continuation
import bolts.Task
import com.android.volley.Request
import com.gcc.smartcity.BuildConfig
import com.gcc.smartcity.network.JsonResponseParser
import com.gcc.smartcity.network.RequestExecutor
import com.gcc.smartcity.network.VolleyRequest
import com.gcc.smartcity.utils.Logger

class RewardsController(
    private val mContext: Context,
    private var rewardsAPIListener: RewardsAPIListener
) {

    private val rewardsAdapterData = ArrayList<RewardsRecyclerViewModel>()

    fun getAdapterData(): ArrayList<RewardsRecyclerViewModel> {

        rewardsAdapterData.clear()

        doRewardsListCall(BuildConfig.HOST + "rewards")

//        val tempRewardModel = RewardsModel(
//            "erhuvdshiuey98fwgi",
//            "5df5af5a4f50ea0012a97916.png",
//            "Free ride everywhere",
//            "free free free",
//            "Insufficient Balance",
//            "Collect 100 more gems to unlock",
//            "22",
//            "8gf62y"
//        )
//        val rewardModel = RewardsRecyclerViewModel(tempRewardModel, 0)
//        rewardsAdapterData.add(rewardModel)
//
//        val tempRewardModel1 = RewardsModel(
//            "erhuvdshiuey98fwgi",
//            "5df5bdc64f50ea0012a9791c.png",
//            "Free ride everywhere",
//            "free free free",
//            "Insufficient Balance",
//            "Collect 100 more gems to unlock",
//            "22",
//            "8gf62y"
//        )
//        val rewardModel1 = RewardsRecyclerViewModel(tempRewardModel1, 0)
//        rewardsAdapterData.add(rewardModel1)
//
//        val tempRewardModel2 = RewardsModel(
//            "erhuvdshiuey98fwgi",
//            "5df8b5f416315900113b2658.jpg",
//            "Free ride everywhere",
//            "free free free",
//            "Insufficient Balance",
//            "Collect 100 more gems to unlock",
//            "22",
//            "8gf62y"
//        )
//        val rewardModel2 = RewardsRecyclerViewModel(tempRewardModel2, 0)
//        rewardsAdapterData.add(rewardModel2)

        return rewardsAdapterData
    }

    private fun doRewardsListCall(url: String) {
        val volleyRequest = VolleyRequest.newInstance<RewardsAPIModel>(Request.Method.GET, url)
        val jsonResponseParser =
            JsonResponseParser<RewardsAPIModel>(RewardsAPIModel::class.java)
        volleyRequest.setResponseParser(jsonResponseParser)
        RequestExecutor.getInstance(mContext).makeRequestCall(volleyRequest).continueWithTask(
            Continuation<Any, Task<Any>> {
                if (!it.isFaulted) {
                    Logger.d("success", "got rewards list")
                    val rewardsAPIModel = it.result as RewardsAPIModel
                    if (rewardsAPIModel.success!! && rewardsAPIModel.rewards!!.isNotEmpty()) {
                        for (i in 0 until (rewardsAPIModel.rewards?.size ?: 0)) {
                            val tempRewardModel = RewardsModel(
                                rewardsAPIModel.rewards?.get(i)?._id.toString(),
                                rewardsAPIModel.rewards?.get(i)?.photoId.toString(),
                                rewardsAPIModel.rewards?.get(i)?.title.toString(),
                                rewardsAPIModel.rewards?.get(i)?.description.toString(),
                                "Insufficient Balance",
                                "Collect 100 more gems to unlock",
                                rewardsAPIModel.rewards?.get(i)?.gems.toString(),
                                rewardsAPIModel.rewards?.get(i)?.validTill.toString()
                            )
                            val rewardModel = RewardsRecyclerViewModel(tempRewardModel, 0)
                            rewardsAdapterData.add(rewardModel)
                        }
                        rewardsAPIListener.onSuccess(rewardsAdapterData)
                    } else if (rewardsAPIModel.success!! && rewardsAPIModel.rewards!!.isEmpty()) {
                        Logger.d("failed", "rewards list is empty")
                        rewardsAPIListener.onFail("There are no rewards right now")
                    }
                } else {
                    Logger.d("failed", "unable to fetch rewards list")
                    rewardsAPIListener.onFail("Unable to fetch rewards right now")
                }
                null
            })
    }
}
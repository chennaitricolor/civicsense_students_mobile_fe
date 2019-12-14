package com.gcc.smartcity.leaderboard

import android.content.Context
import bolts.Continuation
import bolts.Task
import com.android.volley.Request
import com.gcc.smartcity.BuildConfig
import com.gcc.smartcity.network.JsonResponseParser
import com.gcc.smartcity.network.RequestExecutor
import com.gcc.smartcity.network.VolleyRequest
import com.gcc.smartcity.utils.Logger

class LeaderBoardController(private val mContext: Context, private var leaderBoardAPIListener: LeaderBoardAPIListener) {

        private val leaderBoardAdapterData = ArrayList<LeaderBoardRecyclerViewModel>()

        fun getAdapterData(): ArrayList<LeaderBoardRecyclerViewModel> {

            leaderBoardAdapterData.clear()
            doLeaderBoardListCall(BuildConfig.HOST + "user/leaderboard?type=local")

//            val tempWinnerRunnerModel = LeaderBoardWinnerRunnerModel(
//                "Bob",
//                "1000",
//                "FIRST",
//                "Danny",
//                "900",
//                "SECOND",
//                "Bob 2",
//                "900",
//                "THIRD"
//            )
//            val winnerRunnerModel = LeaderBoardRecyclerViewModel(tempWinnerRunnerModel, 0)
//            leaderBoardAdapterData.add(winnerRunnerModel)
////            for (every user)
//            val tempParticipantModel = LeaderBoardParticipantsModel("user 1", "55", "35")
//            val participantModel = LeaderBoardRecyclerViewModel(tempParticipantModel, 1)
//            leaderBoardAdapterData.add(participantModel)
//            val tempParticipantModel1 = LeaderBoardParticipantsModel("user 2", "55", "56")
//            val participantModel1 = LeaderBoardRecyclerViewModel(tempParticipantModel1, 1)
//            leaderBoardAdapterData.add(participantModel1)
//            val tempParticipantModel2 = LeaderBoardParticipantsModel("user 3", "55", "78")
//            val participantModel2 = LeaderBoardRecyclerViewModel(tempParticipantModel2, 1)
//            leaderBoardAdapterData.add(participantModel2)
            return leaderBoardAdapterData
        }

        private fun doLeaderBoardListCall(url: String) {
            val volleyRequest = VolleyRequest.newInstance<LeaderBoardModel>(Request.Method.GET, url)
            val jsonResponseParser =
                JsonResponseParser<LeaderBoardModel>(LeaderBoardModel::class.java)
            volleyRequest.setResponseParser(jsonResponseParser)
            RequestExecutor.getInstance(mContext).makeRequestCall(volleyRequest).continueWithTask(
                Continuation<Any, Task<Any>> {
                    if (!it.isFaulted) {
                        Logger.d("success", "got leaderboard list")
                        val leaderBoardModel = it.result as LeaderBoardModel
                        if (leaderBoardModel.success!! && leaderBoardModel.leaderboard!!.isNotEmpty()) {
                            val tempWinnerRunnerModel = LeaderBoardWinnerRunnerModel(
                                leaderBoardModel.leaderboard?.get(0)?._id.toString(),
                                leaderBoardModel.leaderboard?.get(0)?.rewards.toString(),
                                "FIRST",
                                leaderBoardModel.leaderboard?.get(1)?._id.toString(),
                                leaderBoardModel.leaderboard?.get(1)?.rewards.toString(),
                                "SECOND",
                                leaderBoardModel.leaderboard?.get(2)?._id.toString(),
                                leaderBoardModel.leaderboard?.get(2)?.rewards.toString(),
                                "THIRD"
                            )
                            val winnerRunnerModel =
                                LeaderBoardRecyclerViewModel(tempWinnerRunnerModel, 0)
                            leaderBoardAdapterData.add(winnerRunnerModel)

                            for (i in 3 until (leaderBoardModel.leaderboard?.size ?: 0)) {
                                val tempParticipantModel = LeaderBoardParticipantsModel(
                                    leaderBoardModel.leaderboard?.get(i)?._id.toString(),
                                    leaderBoardModel.leaderboard?.get(i)?.rewards.toString(),
                                    (i + 1).toString()
                                )
                                val participantModel =
                                    LeaderBoardRecyclerViewModel(tempParticipantModel, 1)
                                leaderBoardAdapterData.add(participantModel)
                            }
                            leaderBoardAPIListener.onSuccess(leaderBoardAdapterData)
                        } else if (leaderBoardModel.success!! && leaderBoardModel.leaderboard!!.isEmpty()) {
                            Logger.d("failed", "leader board is empty")
                            leaderBoardAPIListener.onFail("There are no stats to populate the leader board")
                        }
                    } else {
                        Logger.d("failed", "unable to fetch leader board")
                        leaderBoardAPIListener.onFail("Unable to fetch stats for the leader board")
                    }
                    null
                })
        }
}
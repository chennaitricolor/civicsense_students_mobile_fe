package com.gcc.smartcity.leaderboard

import com.gcc.smartcity.preference.SessionStorage
import com.gcc.smartcity.utils.Logger

class LeaderBoardController(
    private var leaderBoardAPIListener: LeaderBoardAPIListener
) {

    private val leaderBoardAdapterData = ArrayList<LeaderBoardRecyclerViewModel>()

    fun getAdapterData(): ArrayList<LeaderBoardRecyclerViewModel> {

        leaderBoardAdapterData.clear()

        val leaderBoardModel = SessionStorage.getInstance().leaderBoardModel
        if (leaderBoardModel.success!! && leaderBoardModel.leaderboard!!.isNotEmpty()) {
            val tempWinnerRunnerModel = LeaderBoardWinnerRunnerModel(
                leaderBoardModel.leaderboard?.get(0)?.name.toString(),
                leaderBoardModel.leaderboard?.get(0)?.rewards.toString(),
                "FIRST",
                leaderBoardModel.leaderboard?.get(1)?.name.toString(),
                leaderBoardModel.leaderboard?.get(1)?.rewards.toString(),
                "SECOND",
                leaderBoardModel.leaderboard?.get(2)?.name.toString(),
                leaderBoardModel.leaderboard?.get(2)?.rewards.toString(),
                "THIRD"
            )
            val winnerRunnerModel =
                LeaderBoardRecyclerViewModel(tempWinnerRunnerModel, 0)
            leaderBoardAdapterData.add(winnerRunnerModel)

            for (i in 3 until (leaderBoardModel.leaderboard?.size ?: 0)) {
                val tempParticipantModel = LeaderBoardParticipantsModel(
                    leaderBoardModel.leaderboard?.get(i)?.name.toString(),
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
        return leaderBoardAdapterData
    }
}
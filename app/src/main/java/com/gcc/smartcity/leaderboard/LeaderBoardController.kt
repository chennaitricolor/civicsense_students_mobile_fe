package com.gcc.smartcity.leaderboard

class LeaderBoardController {

    companion object {
        private val leaderBoardAdapterData = ArrayList<LeaderBoardRecyclerViewModel>()

        fun getAdapterData(): ArrayList<LeaderBoardRecyclerViewModel> {
            leaderBoardAdapterData.clear()
            val tempWinnerRunnerdModel = LeaderBoardWinnerRunnerModel("GOD", "1000", "1", "MOM", "900", "2", "DAD", "900", "3")
            val winnerRunnerModel = LeaderBoardRecyclerViewModel(tempWinnerRunnerdModel,0)
            leaderBoardAdapterData.add(winnerRunnerModel)
//            for (every user)
            val tempParticipantModel = LeaderBoardParticipantsModel("ajith","55", "1")
            val participantModel = LeaderBoardRecyclerViewModel(tempParticipantModel,1)
            leaderBoardAdapterData.add(participantModel)
            return leaderBoardAdapterData
        }
    }
}
package com.gcc.smartcity.leaderboard

class LeaderBoardController {

    companion object {
        private val leaderBoardAdapterData = ArrayList<LeaderBoardRecyclerViewModel>()

        fun getAdapterData(): ArrayList<LeaderBoardRecyclerViewModel> {
            leaderBoardAdapterData.clear()
            val tempWinnerRunnerModel = LeaderBoardWinnerRunnerModel("Bob", "1000", "FIRST", "Danny", "900", "SECOND", "Bob 2", "900", "THIRD")
            val winnerRunnerModel = LeaderBoardRecyclerViewModel(tempWinnerRunnerModel,0)
            leaderBoardAdapterData.add(winnerRunnerModel)
//            for (every user)
            val tempParticipantModel = LeaderBoardParticipantsModel("user 1","55", "35")
            val participantModel = LeaderBoardRecyclerViewModel(tempParticipantModel,1)
            leaderBoardAdapterData.add(participantModel)
            val tempParticipantModel1 = LeaderBoardParticipantsModel("user 2","55", "56")
            val participantModel1 = LeaderBoardRecyclerViewModel(tempParticipantModel1,1)
            leaderBoardAdapterData.add(participantModel1)
            val tempParticipantModel2 = LeaderBoardParticipantsModel("user 3","55", "78")
            val participantModel2 = LeaderBoardRecyclerViewModel(tempParticipantModel2,1)
            leaderBoardAdapterData.add(participantModel2)
            return leaderBoardAdapterData
        }
    }
}
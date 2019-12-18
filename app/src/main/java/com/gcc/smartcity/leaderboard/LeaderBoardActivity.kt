package com.gcc.smartcity.leaderboard

import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gcc.smartcity.BaseActivity
import com.gcc.smartcity.R
import com.gcc.smartcity.fontui.FontTextView
import com.gcc.smartcity.preference.SessionStorage

class LeaderBoardActivity : BaseActivity(), LeaderBoardAPIListener {
    private var leaderBoardListAdapter: LeaderboardListAdapter? = null
    private var leaderBoardRecyclerView: RecyclerView? = null
    private var backArrowButton: ImageView? = null
    private var leaderboardUserName: FontTextView? = null
    private var leaderboardUserPointsEarned: FontTextView? = null
    private var leaderboardUserRanking: FontTextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leader_board)
        leaderBoardRecyclerView = findViewById(R.id.leaderboardRecyclerView)
        leaderboardUserName = findViewById(R.id.leaderboardUserName)
        leaderboardUserPointsEarned = findViewById(R.id.leaderboardUserPointsEarned)
        leaderboardUserRanking = findViewById(R.id.leaderboardUserRanking)
        backArrowButton = findViewById(R.id.backArrowButton)
        backArrowButton?.setOnClickListener {
            goBack()
        }
        setUserLeaderBoardValues()
        setAdapter()
    }

    private fun setUserLeaderBoardValues() {
        leaderboardUserName?.text = SessionStorage.getInstance().userId
        leaderboardUserPointsEarned?.text =
            SessionStorage.getInstance().leaderBoardModel.userRewards.toString()
        leaderboardUserRanking?.text =
            SessionStorage.getInstance().leaderBoardModel.userRank.toString()
    }

    private fun goBack() {
        this.onBackPressed()
    }

    private fun setAdapter() {
        leaderBoardListAdapter = LeaderboardListAdapter()
        leaderBoardRecyclerView?.layoutManager = LinearLayoutManager(this)
        leaderBoardRecyclerView?.adapter = leaderBoardListAdapter
        leaderBoardListAdapter!!.setData(LeaderBoardController(this).getAdapterData())
    }

    override fun onSuccess(leaderBoardRecyclerViewModel: ArrayList<LeaderBoardRecyclerViewModel>) {
        leaderBoardListAdapter!!.setData(leaderBoardRecyclerViewModel)
    }

    override fun onFail(message: String) {
        showErrorDialog(
            getString(R.string.tryAgainLater),
            message,
            getString(R.string.okButtonText)
        )
    }
}

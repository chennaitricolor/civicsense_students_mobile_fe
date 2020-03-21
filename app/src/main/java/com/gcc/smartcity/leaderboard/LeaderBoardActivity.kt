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
    private var leaderBoardUserName: FontTextView? = null
    private var leaderBoardUserPointsEarned: FontTextView? = null
    private var leaderBoardUserRanking: FontTextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leader_board)
        leaderBoardRecyclerView = findViewById(R.id.leaderboardRecyclerView)
        leaderBoardUserName = findViewById(R.id.leaderboardUserName)
        leaderBoardUserPointsEarned = findViewById(R.id.leaderboardUserPointsEarned)
        leaderBoardUserRanking = findViewById(R.id.leaderboardUserRanking)
        backArrowButton = findViewById(R.id.backArrowButton)
        backArrowButton?.setOnClickListener {
            goBack()
        }
        setUserLeaderBoardValues()
        setAdapter()
    }

    private fun setUserLeaderBoardValues() {
        leaderBoardUserName?.text = SessionStorage.getInstance().userId
        leaderBoardUserPointsEarned?.text =
            SessionStorage.getInstance().leaderBoardModel.userRewards.toString()
        val userRank = SessionStorage.getInstance().leaderBoardModel.userRank
        if (userRank != null) {
            if (userRank < 1) {
                leaderBoardUserRanking?.text = "-"
            } else {
                leaderBoardUserRanking?.text =
                    SessionStorage.getInstance().leaderBoardModel.userRank.toString()
            }
        }
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

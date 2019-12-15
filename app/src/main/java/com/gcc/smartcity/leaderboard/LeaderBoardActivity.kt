package com.gcc.smartcity.leaderboard

import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gcc.smartcity.BaseActivity
import com.gcc.smartcity.R

class LeaderBoardActivity : BaseActivity(), LeaderBoardAPIListener {
    private var leaderBoardListAdapter: LeaderboardListAdapter? = null
    private var leaderBoardRecyclerView: RecyclerView? = null
    private var backArrowButton: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leader_board)
        leaderBoardRecyclerView = findViewById(R.id.leaderboardRecyclerView)
        backArrowButton = findViewById(R.id.backArrowButton)
        backArrowButton?.setOnClickListener {
            goBack()
        }
        setAdapter()
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

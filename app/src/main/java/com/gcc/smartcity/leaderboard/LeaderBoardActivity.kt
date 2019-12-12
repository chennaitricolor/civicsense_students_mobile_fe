package com.gcc.smartcity.leaderboard

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gcc.smartcity.R

class LeaderBoardActivity : AppCompatActivity() {
    private var leaderboardListAdapter : LeaderboardListAdapter? = null
    private var leaderBoardRecyclerView : RecyclerView? = null
    private var backArrowButton : ImageView? = null

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
        leaderboardListAdapter = LeaderboardListAdapter()
        leaderBoardRecyclerView?.layoutManager = LinearLayoutManager(this)
        leaderBoardRecyclerView?.adapter = leaderboardListAdapter
        leaderboardListAdapter!!.setData(LeaderBoardController.getAdapterData())
    }
}

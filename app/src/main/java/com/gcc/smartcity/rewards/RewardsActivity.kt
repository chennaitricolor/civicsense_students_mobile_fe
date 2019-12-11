package com.gcc.smartcity.rewards

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gcc.smartcity.R

class RewardsActivity : AppCompatActivity() {
    private var rewardsListAdapter : RewardsListAdapter? = null
    private var rewardsRecyclerView : RecyclerView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewards)
        rewardsRecyclerView = findViewById(R.id.rewardsRecyclerView)
        setAdapter()
    }

    private fun setAdapter() {
        rewardsListAdapter = RewardsListAdapter()
        rewardsRecyclerView?.layoutManager = LinearLayoutManager(this)
        rewardsRecyclerView?.adapter = rewardsListAdapter
        rewardsListAdapter!!.setData(RewardsController.getAdapterData())
    }
}

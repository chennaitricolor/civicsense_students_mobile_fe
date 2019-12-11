package com.gcc.smartcity.rewards

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_rewards_item.view.*

class RewardsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val rewardImage = view.layout_rewardImage
    private val rewardTitle = view.layout_rewardTitle
    private val rewardDescription = view.layout_rewardDescription
    private val balanceStatus = view.layout_balanceStatus
    private val balanceRequired = view.layout_balanceRequired
    private val gemTarget = view.layout_gemTarget

    fun setValues(rewards: RewardsModel) {
        rewardTitle.text = rewards.rewardTitle
        rewardDescription.text = rewards.rewardDescription
        balanceStatus.text = rewards.balanceStatus
        balanceRequired.text = rewards.balanceRequired
        gemTarget.text = rewards.gemTarget
    }
}
package com.gcc.smartcity.rewards

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.gcc.smartcity.BuildConfig
import com.gcc.smartcity.R
import com.gcc.smartcity.leaderboard.LeaderBoardModel
import com.gcc.smartcity.preference.SessionStorage
import kotlinx.android.synthetic.main.layout_rewards_item.view.*


class RewardsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val progressBar: ProgressBar = view.rewardImageProgressBar
    private val rewardImage = view.layout_rewardImage
    private val rewardTitle = view.layout_rewardTitle
    private val rewardDescription = view.layout_rewardDescription
    private val balanceStatus = view.layout_balanceStatus
    private val balanceRequired = view.layout_balanceRequired
    private val gemTarget = view.layout_gemTarget
    private var leaderBoardModel: LeaderBoardModel? = null

    fun setValues(rewards: RewardsModel) {
        leaderBoardModel = SessionStorage.getInstance().leaderBoardModel
        progressBar.visibility = View.VISIBLE
        Glide.with(rewardImage.context)
            .load(
                BuildConfig.HOST + java.lang.String.format(
                    "images/%s?isAsset=true",
                    rewards.imageData
                )
            )
            .error(R.drawable.ic_error_outline_black_24dp)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    return false // important to return false so the error placeholder can be placed
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    return false
                }
            })
            .into(rewardImage)
        rewardTitle.text = rewards.rewardTitle
        rewardDescription.text = rewards.rewardDescription
        balanceStatus.text = rewards.balanceStatus

        if (leaderBoardModel!= null) {
            if (leaderBoardModel?.userRewards!! > rewards.gemTarget.toInt()) {
                balanceRequired.text = "YOU HAVE ENOUGH GEMS TO CLAIM THIS REWARD"
                balanceStatus.text = "SUFFICIENT BALANCE"
            } else {
                val rewardsBal =
                    rewards.gemTarget.toInt() - leaderBoardModel?.userRewards!!
                balanceRequired.text = "COLLECT $rewardsBal MORE GEMS TO UNLOCK"
                balanceStatus.text = "INSUFFICIENT BALANCE"
            }
        }

        gemTarget.text = rewards.gemTarget
    }
}
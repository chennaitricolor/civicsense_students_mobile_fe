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
import com.gcc.smartcity.network.PersistantCookieStore
import kotlinx.android.synthetic.main.layout_rewards_item.view.*
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy


class RewardsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val progressBar: ProgressBar = view.rewardImageProgressBar
    private val rewardImage = view.layout_rewardImage
    private val rewardTitle = view.layout_rewardTitle
    private val rewardDescription = view.layout_rewardDescription
    private val balanceStatus = view.layout_balanceStatus
    private val balanceRequired = view.layout_balanceRequired
    private val gemTarget = view.layout_gemTarget

    fun setValues(rewards: RewardsModel) {
        progressBar.visibility = View.VISIBLE
        Glide.with(rewardImage.context)
            .load(
                BuildConfig.HOST + java.lang.String.format(
                    "images/%s?isAsset=true",
                    rewards.imageData
                ))
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
        balanceRequired.text = rewards.balanceRequired
        gemTarget.text = rewards.gemTarget
    }
}
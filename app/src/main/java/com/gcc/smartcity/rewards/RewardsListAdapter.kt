package com.gcc.smartcity.rewards

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gcc.smartcity.R

class RewardsListAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var rewardsModelList: ArrayList<RewardsRecyclerViewModel> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_rewards_item, parent, false)
        return RewardsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return rewardsModelList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val holderData = rewardsModelList[position]

        when (holderData.viewType) {
            0 -> {
                val tempHolder = holder as RewardsViewHolder
                tempHolder.setValues(holderData.data as RewardsModel)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return rewardsModelList[position].viewType
    }

    fun setData(model: ArrayList<RewardsRecyclerViewModel>) {
        rewardsModelList = model
        notifyDataSetChanged()
    }

}
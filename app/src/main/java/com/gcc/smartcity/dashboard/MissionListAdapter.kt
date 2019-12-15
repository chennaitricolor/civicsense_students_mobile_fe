package com.gcc.smartcity.dashboard

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import com.gcc.smartcity.R


class MissionListAdapter(var context: Context, private var dataSet: ArrayList<MissionModel>) :
    BaseAdapter() {


    private inner class MissionViewHolder(view: View) {
        internal var menuName: TextView
        internal var gemCount: TextView
        internal var missionParent: RelativeLayout

        init {
            menuName = view.findViewById(R.id.missionName) as TextView
            gemCount = view.findViewById(R.id.missionGemCount) as TextView
            missionParent = view.findViewById(R.id.missionParent) as RelativeLayout
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var rowView = convertView
        val missionViewHolder: MissionViewHolder

        if (convertView == null) {
            rowView = LayoutInflater.from(context)
                .inflate(R.layout.layout_mission, parent, false)
            missionViewHolder = MissionViewHolder(rowView)
            rowView.tag = missionViewHolder
        } else {
            missionViewHolder = rowView?.tag as MissionViewHolder
        }

        // get current item to be displayed
        val missionModel = getItem(position) as MissionModel
        missionViewHolder.menuName.text = missionModel.campaignName
        missionViewHolder.gemCount.text = missionModel.rewards.toString()
        missionViewHolder.missionParent.setOnClickListener {
            launchCameraForImageCapture(missionModel)
        }
        rowView?.tag = missionViewHolder
        return rowView!!
    }

    private fun launchCameraForImageCapture(missionModel: MissionModel) {
        val intent = Intent(context, ImageCaptureActivity::class.java)
        intent.putExtra("_id", missionModel._id)
        intent.putExtra("rewards", missionModel.rewards.toString())
        context.startActivity(intent)
    }

    override fun getItem(position: Int): Any {
        return dataSet[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataSet.size
    }


}
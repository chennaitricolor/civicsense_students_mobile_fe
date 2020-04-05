package com.gcc.smartcity.dashboard

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import bolts.Task
import com.gcc.smartcity.BuildConfig
import com.gcc.smartcity.R
import com.gcc.smartcity.dashboard.model.NewMissionListModel
import com.gcc.smartcity.preference.SessionStorage


class MissionListAdapter(var context: Context, private var dataSet: ArrayList<MissionModel>) :
    BaseAdapter() {

    private var mNewMissionDetailsController: NewMissionDetailsController? = null

    init {
        this.mNewMissionDetailsController = NewMissionDetailsController(context)
    }

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

        val missionModel = getItem(position) as MissionModel
        missionViewHolder.menuName.text = missionModel.campaignName
        missionViewHolder.gemCount.text = missionModel.rewards.toString()
        missionViewHolder.missionParent.setOnClickListener {
            doIndividualMissionDetailsCall(missionModel._id)
        }
        rowView?.tag = missionViewHolder
        return rowView!!
    }

    private fun doIndividualMissionDetailsCall(id: String) {
        mNewMissionDetailsController?.doIndividualMissionInformationCall(
            BuildConfig.HOST + java.lang.String.format(
                "user/tasks/%s",
                id
            )
        )?.continueWithTask { task ->
            afterIndividualMissionDetailsCall(task)
        }
    }

    private fun afterIndividualMissionDetailsCall(task: Task<Any>): Task<Any>? {
        if (task.isFaulted) {
            Toast.makeText(context, "Unable to fetch details for the task", Toast.LENGTH_LONG)
                .show()
            task.makeVoid()
        } else {
            val missionInfoModel = task.result as NewMissionListModel
            if (missionInfoModel.success!!) {
                SessionStorage.getInstance().newMissionListModel = missionInfoModel
                launchRulesActivity()
            } else {
                Toast.makeText(context, "Unable to fetch details for the task", Toast.LENGTH_LONG)
                    .show()
            }
        }

        return null
    }

    private fun launchRulesActivity() {
        val intent = Intent(context, RulesActivity::class.java)
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
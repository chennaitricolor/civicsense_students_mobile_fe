package com.gcc.smartcity.navigationdrawer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gcc.smartcity.R

class NavDrawerListAdapter(
    private var navDrawerListItem: ArrayList<NavDrawerListItem>,
    private var OnRecyclerSelectedListener: OnRecyclerSelectedListener
) :
    RecyclerView.Adapter<NavDrawerListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val context = parent.context
        val inflater = LayoutInflater.from(context)

        val menuView = inflater.inflate(R.layout.layout_list_item, parent, false)

        return ViewHolder(menuView)

    }

    override fun getItemCount(): Int {
        return navDrawerListItem.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val holderData = navDrawerListItem[position]

        val textView = viewHolder.menuName
        textView.text = holderData.menuName
        val imageView = viewHolder.menuImage
        imageView.setImageResource(holderData.imgResource)
        val view = viewHolder.dataView
        view.setOnClickListener { OnRecyclerSelectedListener.onSelected(position) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var menuName: TextView = itemView.findViewById(R.id.lst_itm_menuname)
        var menuImage: ImageView = itemView.findViewById(R.id.lst_itm_menuimg)
        var dataView: LinearLayout = itemView.findViewById(R.id.lstView)
    }
}
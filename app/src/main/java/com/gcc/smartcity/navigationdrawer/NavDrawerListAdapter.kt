package com.gcc.smartcity.navigationdrawer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gcc.smartcity.R


class NavDrawerListAdapter(var navDrawerListItem: ArrayList<NavDrawerListItem>) :
    RecyclerView.Adapter<NavDrawerListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the custom layout
        val menuView = inflater.inflate(R.layout.layout_list_item, parent, false)

        // Return a new holder instance
        return ViewHolder(menuView)

    }


    override fun getItemCount(): Int {
        return navDrawerListItem.size
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val holderData = navDrawerListItem[position]

        val textView = viewHolder.menuName
        textView.setText(holderData.menuName)
        val ImageView = viewHolder.menuImage
        ImageView.setImageResource(holderData.imgResource)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        lateinit var menuName: TextView
        lateinit var menuImage: ImageView

        init {

            menuName = itemView.findViewById(R.id.lst_itm_menuname)
            menuImage = itemView.findViewById(R.id.lst_itm_menuimg)
        }
        // Stores the itemView in a public final member variable that can be used
        // to access the context from any ViewHolder instance.
    }
}

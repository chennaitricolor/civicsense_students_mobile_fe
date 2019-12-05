package com.gcc.smartcity.navigationdrawer

import android.content.Context
import com.gcc.smartcity.R

class NavigationController(context: Context) {

    fun getAdapterData(): ArrayList<NavDrawerListItem> {
        var drawerListData = ArrayList<NavDrawerListItem>()

        var navDrawerListItem = NavDrawerListItem(R.drawable.ic_howtoplay, "How to Play")
        drawerListData.add(navDrawerListItem)
        navDrawerListItem = NavDrawerListItem(R.drawable.ic_invite, "Invite")
        drawerListData.add(navDrawerListItem)
        navDrawerListItem = NavDrawerListItem(R.drawable.ic_faq, "FAQ's")
        drawerListData.add(navDrawerListItem)
        navDrawerListItem = NavDrawerListItem(R.drawable.ic_gethelp, "Get Help")
        drawerListData.add(navDrawerListItem)
        navDrawerListItem = NavDrawerListItem(R.drawable.ic_rateus, "Rate Us")
        drawerListData.add(navDrawerListItem)
        return drawerListData
    }

}
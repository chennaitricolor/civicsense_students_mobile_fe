package com.gcc.smartcity.navigationdrawer

import android.content.Context
import com.gcc.smartcity.R

class NavigationController(var context: Context) {

    fun getAdapterData(): ArrayList<NavDrawerListItem> {
        val drawerListData = ArrayList<NavDrawerListItem>()

//        var navDrawerListItem = NavDrawerListItem(
//            R.drawable.ic_howtoplay,
//            context.getString(R.string.drawer_menu_howtoplay)
//        )
//        drawerListData.add(navDrawerListItem)

        var navDrawerListItem = NavDrawerListItem(R.drawable.ic_invite, context.getString(R.string.drawer_menu_invite))
        drawerListData.add(navDrawerListItem)

//        navDrawerListItem = NavDrawerListItem(R.drawable.ic_faq, context.getString(R.string.drawer_menu_faq))
//        drawerListData.add(navDrawerListItem)

//        navDrawerListItem = NavDrawerListItem(R.drawable.ic_gethelp, context.getString(R.string.drawer_menu_help))
//        drawerListData.add(navDrawerListItem)

        navDrawerListItem = NavDrawerListItem(R.drawable.ic_rateus, context.getString(R.string.drawer_menu_rateus))
        drawerListData.add(navDrawerListItem)

//        if (SessionStorage.getInstance().leaderBoardStatus) {
//            navDrawerListItem = NavDrawerListItem(
//                R.drawable.ic_leadership,
//                context.getString(R.string.drawer_menu_leaderboard)
//            )
//            drawerListData.add(navDrawerListItem)
//        }

//        navDrawerListItem = NavDrawerListItem(R.drawable.ic_rewards, context.getString(R.string.drawer_menu_rewards))
//        drawerListData.add(navDrawerListItem)

        navDrawerListItem = NavDrawerListItem(R.drawable.ic_faq, context.getString(R.string.drawer_menu_aboutus))
        drawerListData.add(navDrawerListItem)

        navDrawerListItem = NavDrawerListItem(R.drawable.ic_gethelp, context.getString(R.string.drawer_menu_help))
        drawerListData.add(navDrawerListItem)

        return drawerListData
    }

}
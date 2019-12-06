package com.gcc.smartcity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.gcc.smartcity.leaderboard.LeaderBoardActivity
import com.gcc.smartcity.navigationdrawer.NavDrawerListAdapter
import com.gcc.smartcity.navigationdrawer.NavDrawerListItem
import com.gcc.smartcity.navigationdrawer.NavigationController
import com.gcc.smartcity.navigationdrawer.onRecyclerSelecetedListener
import com.gcc.smartcity.utils.Logger
import kotlinx.android.synthetic.main.activity_navigation_drawer.*

abstract class NavigationDrawerActivity : AppCompatActivity(), onRecyclerSelecetedListener {

    val TAG = NavigationDrawerActivity::class.java.name
    lateinit var drawerList: ArrayList<NavDrawerListItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_drawer)
        setActions()
        setAdapter()
    }

    override fun onSelected(pos: Int) {
        switchScreen(drawerList.get(pos).menuName)
    }

    private fun switchScreen(menuName: String) {
        when (menuName) {
            getString(R.string.drawer_menu_howtoplay) -> Logger.d("How to play")
            getString(R.string.drawer_menu_rateus) -> Logger.d("Rate Us")
            getString(R.string.drawer_menu_help) -> Logger.d("Help")
            getString(R.string.drawer_menu_faq) -> Logger.d("FAQ")
            getString(R.string.drawer_menu_invite) -> Logger.d("Invite")
            getString(R.string.drawer_menu_rewards) -> Logger.d("Rewards")
            getString(R.string.drawer_menu_leaderboard) -> {
                Logger.d("Leaderboard")
                val intent = Intent(this, LeaderBoardActivity::class.java)
                startActivity(intent)
            }

        }
    }

    private fun setAdapter() {
        drawerList = NavigationController(this).getAdapterData()
        // Create adapter passing in the sample user data
        val adapter = NavDrawerListAdapter(drawerList, this)
        // Attach the adapter to the recyclerView to populate items
        navListMenu.adapter = adapter
        // Set layout manager to position the items
        navListMenu.layoutManager = LinearLayoutManager(this)
    }

    private fun setActions() {
        mHambugerImg.setOnClickListener { mDrawerLayout.openDrawer(GravityCompat.START) }
    }

    /**
     * Abstract methods
     */
    protected abstract fun shouldShowNavigationDrawer(): Boolean

    protected abstract fun shouldShowToolbarBackButton(): Boolean

    protected fun setMainContentView(layout: Int) {
        try {
            if (mFlContentLayout != null) {
                Logger.d(TAG, "setMainContentView - setting the Layout with ID: $layout")
                mFlContentLayout.addView(layoutInflater.inflate(layout, null))
            }
        } catch (e: Exception) {
            Logger.d(e.localizedMessage)
            e.message?.let { Logger.d(it) }
        }

    }
}
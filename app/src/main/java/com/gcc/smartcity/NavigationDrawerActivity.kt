package com.gcc.smartcity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.gcc.smartcity.navigationdrawer.NavDrawerListAdapter
import com.gcc.smartcity.navigationdrawer.NavigationController
import com.gcc.smartcity.utils.Logger
import kotlinx.android.synthetic.main.activity_navigation_drawer.*

abstract class NavigationDrawerActivity : AppCompatActivity() {

    val TAG = NavigationDrawerActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_drawer)
        setActions()
        setAdapter()
    }

    private fun setAdapter() {
        // Create adapter passing in the sample user data
        val adapter = NavDrawerListAdapter(NavigationController(this).getAdapterData())
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
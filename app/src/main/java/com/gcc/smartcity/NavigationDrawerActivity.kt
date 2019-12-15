package com.gcc.smartcity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.gcc.smartcity.leaderboard.LeaderBoardActivity
import com.gcc.smartcity.navigationdrawer.NavDrawerListAdapter
import com.gcc.smartcity.navigationdrawer.NavDrawerListItem
import com.gcc.smartcity.navigationdrawer.NavigationController
import com.gcc.smartcity.navigationdrawer.OnRecyclerSelectedListener
import com.gcc.smartcity.rewards.RewardsActivity
import com.gcc.smartcity.utils.AlertDialogBuilder
import com.gcc.smartcity.utils.Logger
import com.gcc.smartcity.utils.OnSingleBtnDialogListener
import kotlinx.android.synthetic.main.activity_navigation_drawer.*


abstract class NavigationDrawerActivity : AppCompatActivity(), OnRecyclerSelectedListener {

    private val TAG = NavigationDrawerActivity::class.java.name
    private lateinit var drawerList: ArrayList<NavDrawerListItem>
    private val mOnSingleBtnDialogListener: OnSingleBtnDialogListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_drawer)
        setActions()
        setAdapter()
    }

    override fun onSelected(pos: Int) {
        mDrawerLayout.closeDrawer(GravityCompat.START)
        switchScreen(drawerList.get(pos).menuName)
    }

    private fun switchScreen(menuName: String) {
        when (menuName) {
            getString(R.string.drawer_menu_howtoplay) -> Logger.d("How to play")
            getString(R.string.drawer_menu_invite) -> {
                shareApp(this)
            }
            getString(R.string.drawer_menu_faq) -> Logger.d("FAQ")
            getString(R.string.drawer_menu_help) -> Logger.d("Help")
            getString(R.string.drawer_menu_rateus) -> {
                openAppRating(this)
            }
            getString(R.string.drawer_menu_rewards) -> {
                Logger.d("Rewards")
                val intent = Intent(this, RewardsActivity::class.java)
                startActivity(intent)
            }
            getString(R.string.drawer_menu_leaderboard) -> {
                Logger.d("LeaderBoard")
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

    open fun shareApp(context: Context) {
        val appId: String = context.packageName
        val myIntent = Intent(Intent.ACTION_SEND)
        myIntent.setType("text/plain")
        val shareBody =
            "I use AgentX and I love it. It helps in ensuring a safer city. Download it at https://play.google.com/store/apps/details?id=$appId"
        val shareSub = "Link to install AgentX App"
        myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub)
        myIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(
            Intent.createChooser(
                myIntent,
                "Share \"AgentX app\" via"
            )
        )
    }

    open fun openAppRating(context: Context) {
        // you can also use BuildConfig.APPLICATION_ID
        val appId: String = context.packageName
        val rateIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("market://details?id=$appId")
        )
        var marketFound = false
        // find all applications able to handle our rateIntent
        val otherApps: List<ResolveInfo> = context.packageManager
            .queryIntentActivities(rateIntent, 0)
        for (otherApp in otherApps) { // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName
                == "com.android.vending"
            ) {
                val otherAppActivity = otherApp.activityInfo
                val componentName = ComponentName(
                    otherAppActivity.applicationInfo.packageName,
                    otherAppActivity.name
                )
                // make sure it does NOT open in the stack of your activity
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                // task reparenting if needed
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                // if the Google Play was already open in a search result
                //  this make sure it still go to the app page you requested
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                // this make sure only the Google Play app is allowed to
                // intercept the intent
                rateIntent.component = componentName
                try {
                    context.startActivity(rateIntent)
                    marketFound = true
                } catch (e: Exception) {
                    Toast.makeText(this, getString(R.string.tryAgainLater), Toast.LENGTH_SHORT)
                        .show()
                }
                break
            }
        }
        // if GP not present on device, open web browser
        if (!marketFound) {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$appId")
            )
            try {
                context.startActivity(webIntent)
            } catch (e: Exception) {
                Toast.makeText(this, getString(R.string.tryAgainLater), Toast.LENGTH_SHORT).show()
            }
        }
    }

    open fun showErrorDialog(
        title: String?,
        message: String?,
        buttonText: String?
    ) {
        AlertDialogBuilder.getInstance()
            .showErrorDialog(title, message, buttonText, this, mOnSingleBtnDialogListener)
    }
}
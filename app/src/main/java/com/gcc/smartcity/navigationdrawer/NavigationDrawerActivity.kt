package com.gcc.smartcity.navigationdrawer

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.gcc.smartcity.R
import com.gcc.smartcity.aboutus.AboutUs
import com.gcc.smartcity.fontui.FontTextView
import com.gcc.smartcity.leaderboard.LeaderBoardActivity
import com.gcc.smartcity.leaderboard.LeaderBoardModel
import com.gcc.smartcity.preference.SessionStorage
import com.gcc.smartcity.rewards.RewardsActivity
import com.gcc.smartcity.user.UserModel
import com.gcc.smartcity.utils.AlertDialogBuilder
import com.gcc.smartcity.utils.Logger
import com.gcc.smartcity.utils.OnSingleBtnDialogListener
import kotlinx.android.synthetic.main.activity_navigation_drawer.*

abstract class NavigationDrawerActivity : AppCompatActivity(), OnRecyclerSelectedListener {

    private val TAG = NavigationDrawerActivity::class.java.name
    private lateinit var drawerList: ArrayList<NavDrawerListItem>
    private val mOnSingleBtnDialogListener: OnSingleBtnDialogListener? = null
    private var userStatsHolder: LinearLayout? = null
    private var userNameDrawer: FontTextView? = null
    private var gemsCountDrawer: FontTextView? = null
    private var rankCountDrawer: FontTextView? = null
    private var leaderBoardModel: LeaderBoardModel? = null
    private var userDetailsModel: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_drawer)
        userStatsHolder = findViewById(R.id.userStats)
        userNameDrawer = findViewById(R.id.usernameDrawer)
        gemsCountDrawer = findViewById(R.id.gemsCountDrawer)
        rankCountDrawer = findViewById(R.id.rankCountDrawer)
        leaderBoardModel = SessionStorage.getInstance().leaderBoardModel
        userDetailsModel = SessionStorage.getInstance().userModel
        if (leaderBoardModel != null) {
            setUserID()
            userStatsVisibilityModifier()
        }

        setActions()
        setAdapter()
    }

    private fun setUserID() {
        val userId = SessionStorage.getInstance().userId
        if (userDetailsModel != null && !userDetailsModel?.name.isNullOrEmpty()) {
            userNameDrawer?.text = userDetailsModel?.name
        } else {
            userNameDrawer?.text = userId
        }
    }

    private fun userStatsVisibilityModifier() {
        userStatsHolder?.visibility = View.GONE

//        if (SessionStorage.getInstance().leaderBoardStatus == false) {
//            userStatsHolder?.visibility = View.GONE
//        } else {
//            gemsCountDrawer?.text =
//                leaderBoardModel?.userRewards.toString()
//            val userRank = leaderBoardModel?.userRank
//            if (userRank != null) {
//                if (userRank < 1) {
//                    rankCountDrawer?.text = "-"
//                } else {
//                    rankCountDrawer?.text =
//                        leaderBoardModel?.userRank.toString()
//                }
//            }
//        }
    }

    override fun onSelected(pos: Int) {
        mDrawerLayout.closeDrawer(GravityCompat.START)
        switchScreen(drawerList.get(pos).menuName)
    }

    private fun switchScreen(menuName: String) {
        when (menuName) {
            getString(R.string.drawer_menu_howtoplay) -> {

                if (SessionStorage.getInstance().rootModel != null && SessionStorage.getInstance().rootModel.links != null && SessionStorage.getInstance().rootModel.links?.howToPlay != null) {
                    watchYoutubeVideo(
                        this,
                        "" + SessionStorage.getInstance().rootModel.links?.howToPlay
                    )
                }
            }
            getString(R.string.drawer_menu_invite) -> {
                shareApp(this)
            }
//            getString(R.string.drawer_menu_faq) -> Logger.d("FAQ")
            getString(R.string.drawer_menu_help) -> {
                Logger.d("Help")
                openMailApp()
            }

            getString(R.string.drawer_menu_rateus) -> {
                Logger.d("Rate Us")
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
            getString(R.string.drawer_menu_aboutus) -> {
                Logger.d("About us")
                val intent = Intent(this, AboutUs::class.java)
                startActivity(intent)
            }
        }
    }

    private fun openMailApp() {
        val mailto = "mailto:gccsecretagentx@gmail.com"

        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse(mailto)

        try {
            startActivity(emailIntent)
        } catch (e: ActivityNotFoundException) {
            Logger.d("Unable to open mail intent chooser")
        }
    }

    private fun setAdapter() {
        drawerList = NavigationController(this).getAdapterData()
        val adapter = NavDrawerListAdapter(drawerList, this)
        navListMenu.adapter = adapter
        navListMenu.layoutManager = LinearLayoutManager(this)
    }

    private fun setActions() {
        mHambugerImg.setOnClickListener { mDrawerLayout.openDrawer(GravityCompat.START) }
    }

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
        myIntent.type = "text/plain"
        val shareBody =
            "I use the GCC - Corona Monitoring app and I love it. It helps in ensuring a safer city. Download it at https://play.google.com/store/apps/details?id=$appId"
        val shareSub = "Link to install GCC - Corona Monitoring App"
        myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub)
        myIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(
            Intent.createChooser(
                myIntent,
                "Share \"GCC - Corona Monitoring app\" via"
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

    open fun watchYoutubeVideo(context: Context, url: String) {
//        val appIntent =
//            Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$id"))
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.youtube.com/watch?v=Avt-1ucQhps")

        )
        context.startActivity(webIntent)
//        try {
//            context.startActivity(appIntent)
//        } catch (ex: ActivityNotFoundException) {
//            context.startActivity(webIntent)
//        }
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
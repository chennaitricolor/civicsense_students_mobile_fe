package com.gcc.smartcity.maintenance

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import bolts.Task
import com.gcc.smartcity.R
import com.gcc.smartcity.root.RootActivity
import com.gcc.smartcity.root.RootController
import kotlinx.android.synthetic.main.activity_maintenance.*

class MaintenanceActivity : AppCompatActivity() {
    private var mRootController: RootController? = null

    init {
        mRootController = RootController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maintenance)
        if (intent.extras != null) {
            if (intent.extras!!.getString("reason") == "userLocationInvalid") {
                errorHeader.text = intent.extras!!.getString("header")
                errorTitle.text = intent.extras!!.getString("userLocationValidationErrorMessage")
                maintenanceImage.setImageResource(R.drawable.unsupported_location)
            }
        }
        errorButton.setOnClickListener {
            errorButton.visibility = View.GONE
            progressView.visibility = View.VISIBLE
            doRootCall()
        }
    }

    private fun doRootCall() {
        mRootController?.doRootCall()?.continueWith { it ->
            afterRootCall(it)
        }
    }

    private fun afterRootCall(task: Task<Any>) {
        if (!task.isFaulted) {
            callNextActivity(RootActivity::class.java)
        } else {
            errorButton.visibility = View.VISIBLE
            progressView.visibility = View.GONE
        }

    }

    private fun callNextActivity(activityClass: Class<*>) {
        val i = Intent(this, activityClass)
        intent.clearStack()
        startActivity(i)
        finish()
    }

    private fun Intent.clearStack() {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}

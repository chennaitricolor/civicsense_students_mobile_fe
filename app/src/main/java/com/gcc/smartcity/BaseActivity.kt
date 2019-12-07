package com.gcc.smartcity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gcc.smartcity.utils.AlertDialogBuilder
import com.gcc.smartcity.utils.Logger
import com.gcc.smartcity.utils.OnSingleBtnDialogListener
import kotlinx.android.synthetic.main.activity_base_map.*

open class BaseActivity : AppCompatActivity() {

    private val mOnSingleBtnDialogListener: OnSingleBtnDialogListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_map)
    }

    protected fun setView(layout: Int) {
        try {
            if (InflatorLayout != null) {
                InflatorLayout.addView(layoutInflater.inflate(layout, null))
            }
        } catch (e: Exception) {
            Logger.d(e.localizedMessage)
            e.message?.let { Logger.d(it) }
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
package com.gcc.smartcity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.gcc.smartcity.utils.AlertDialogBuilder
import com.gcc.smartcity.utils.Logger
import com.gcc.smartcity.utils.OnSingleBtnDialogListener
import kotlinx.android.synthetic.main.activity_base.*

open class BaseActivity : AppCompatActivity() {

    private val mOnSingleBtnDialogListener: OnSingleBtnDialogListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showLoader(status: Boolean) {
        if (status) {
            InflatorLayout.visibility = View.GONE
            base_loader_layout.visibility = View.VISIBLE
        } else {
            InflatorLayout.visibility = View.VISIBLE
            base_loader_layout.visibility = View.GONE
        }
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

    fun hideSoftKeyBoard() {
        val inputManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText) {
            if (currentFocus != null) {
                inputManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            }
        }
    }

    fun Intent.clearStack() {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}
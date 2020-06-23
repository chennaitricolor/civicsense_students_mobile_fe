package com.gcc.smartcity.intro

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.gcc.smartcity.R
import com.gcc.smartcity.preference.SessionStorage
import com.heinrichreimersoftware.materialintro.app.SlideFragment
import com.heinrichreimersoftware.materialintro.slide.Slide
import kotlinx.android.synthetic.main.fragment_terms_and_conditions.*

class TermsAndConditionsFragment : SlideFragment(), Slide {

    private var checkBox: CheckBox? = null
    private lateinit var vw: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vw=inflater.inflate(R.layout.fragment_terms_and_conditions, container, false)
        val termsTxt=vw.findViewById<TextView>(R.id.txt_termsCondition)
        termsTxt.text = Html.fromHtml(SessionStorage.getInstance().rootModel.termsAndCondition)
        return vw
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkBox = terms_and_conditions_checkbox
    }

    override fun getBackground(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBackgroundDark(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFragment(): Fragment {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canGoForward(): Boolean {
        return if (checkBox?.isChecked == true) {
            true
        } else {
            Toast.makeText(context, "Please swipe up to read through the agreement.", Toast.LENGTH_SHORT).show()
            false
        }
    }

//    override val isPolicyRespected: Boolean
//        get() = checkBox?.isChecked ?: false
//
//
//    override fun onUserIllegallyRequestedNextPage() {
//        Toast.makeText(context, R.string.policy_error, Toast.LENGTH_SHORT).show()
//    }
}

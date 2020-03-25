package com.gcc.smartcity.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.gcc.smartcity.R
import com.github.paolorotolo.appintro.ISlidePolicy
import kotlinx.android.synthetic.main.fragment_terms_and_conditions.*

class TermsAndConditionsFragment : Fragment(), ISlidePolicy {

    private var checkBox: CheckBox? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_terms_and_conditions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkBox = terms_and_conditions_checkbox
    }

    override val isPolicyRespected: Boolean
        get() = checkBox?.isChecked ?: false


    override fun onUserIllegallyRequestedNextPage() {
        Toast.makeText(context, R.string.policy_error, Toast.LENGTH_SHORT).show()
    }
}

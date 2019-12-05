package com.gcc.smartcity.userregistartion

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.TextView
import android.widget.Toast
import com.gcc.smartcity.BaseActivity
import com.gcc.smartcity.R
import com.gcc.smartcity.fontui.FontEditText
import kotlinx.android.synthetic.main.activity_select_avatar.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.login_RDBTNPasswordshow
import java.text.SimpleDateFormat
import java.util.*


class SignUpActivity : BaseActivity() {
    private var dob: FontEditText? = null
    private var email: FontEditText? = null
    private var isEmailValid: Boolean = false
    private var password: FontEditText? = null
    private var username: FontEditText? = null
    private var isPasswordStrengthValid: Boolean = false
    private var name: FontEditText? = null
    private val myCalendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setView(R.layout.activity_sign_up)

        name = findViewById(R.id.Name)
        dob = findViewById(R.id.dob)
        email = findViewById(R.id.Email)
        password = findViewById(R.id.password)
        username = findViewById(R.id.Username)

        val emailPattern =
            "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))\$"
        val passwordPattern =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$"

        showVisiblePasswordButton(false)

        buttonEffect(NextBtn)

        NextBtn.setOnClickListener {
            //  if (nameValidate?.text.toString().isNotEmpty() && (isEmailValid && emailValidate?.text.toString().isNotEmpty()) && (isPasswordStrengthValid && passwordStrengthValidate?.text.toString().isNotEmpty()) && dobValidate?.text.toString().isNotEmpty()) {
            setContentView(R.layout.activity_select_avatar)
            selectAvatar()
            //            } else {
//            showToast("Please check whether you have entered all the details correctly")
//                Toast.makeText(applicationContext,"Please check whether you have entered all the details correctly",Toast.LENGTH_SHORT).show()
//            }
        }



        email?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.matches((emailPattern).toRegex()) && s.isNotEmpty()) {
                    Log.d("success", "valid")
                    isEmailValid = true
                    email?.setBackgroundResource(R.drawable.bg_border_edittext)
                } else if (s.isEmpty()) {
                    isEmailValid = false
                    email?.setBackgroundResource(R.drawable.bg_border_edittext)
                } else {
                    Log.d("failure", "FAIL")
                    isEmailValid = false
                    email?.setBackgroundResource(R.drawable.bg_border_edittext_wrong)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // other stuffs
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // other stuffs
            }
        })

        username?.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (!hasFocus && username?.text.toString() != "") {
                validateUserName()
            }
        }

        password?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.toString().isNotEmpty()) {
                    showVisiblePasswordButton(true)
                } else {
                    showVisiblePasswordButton(false)
                }
                if (s.matches((passwordPattern).toRegex()) && s.isNotEmpty()) {
                    Log.d("success", "valid")
                    isPasswordStrengthValid = true
                    password?.setBackgroundResource(R.drawable.bg_border_edittext)
                } else if (s.isEmpty()) {
                    isPasswordStrengthValid = false
                    password?.setBackgroundResource(R.drawable.bg_border_edittext)
                } else {
                    Log.d("failure", "FAIL")
                    isPasswordStrengthValid = false
                    password?.setBackgroundResource(R.drawable.bg_border_edittext_wrong)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // other stuffs
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // other stuffs
            }
        })

        val date =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabel()
            }

        dob?.setOnClickListener {
            val dialog = DatePickerDialog(
                this@SignUpActivity, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )

            dialog.datePicker.maxDate = Calendar.getInstance().timeInMillis - 1000
            dialog.show()
        }


    }

    private fun selectAvatar() {
        buttonEffect(SubmitBtn)
        SubmitBtn.setOnClickListener {
            val intent = Intent(this, OTPVerifyActivity::class.java)
            startActivity(intent)
        }
    }

    private fun buttonEffect(button: View) {
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.background.setColorFilter(
                        Color.parseColor("#7aa133"),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    v.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    v.background.clearColorFilter()
                    v.invalidate()
                }
            }
            false
        }
    }

    private fun validateUserName() {
        password?.setBackgroundResource(R.drawable.bg_border_edittext)
    }

    private fun showVisiblePasswordButton(status: Boolean) {
        if (status) {
            login_RDBTNPasswordshow.visibility = View.VISIBLE
        } else {
            login_RDBTNPasswordshow.visibility = View.INVISIBLE

        }
        login_RDBTNPasswordshow.setOnCheckedChangeListener { _, b ->
            if (b) {
                password?.transformationMethod = PasswordTransformationMethod.getInstance()
                password?.setSelection(password!!.text?.length!!)
            } else {
                password?.transformationMethod = HideReturnsTransformationMethod.getInstance()
                password?.setSelection(password!!.text?.length!!)
            }
        }
    }

    private fun updateLabel() {
        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        dob?.setText(sdf.format(myCalendar.time))
    }

    private fun showToast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
        val v =
            toast.view.findViewById<View>(R.id.message) as TextView
        v.gravity = Gravity.CENTER
        toast.show()
    }
}

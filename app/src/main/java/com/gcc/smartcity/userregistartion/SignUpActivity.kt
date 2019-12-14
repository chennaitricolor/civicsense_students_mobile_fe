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
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.Toast
import bolts.Task
import com.gcc.smartcity.BaseActivity
import com.gcc.smartcity.BuildConfig
import com.gcc.smartcity.R
import com.gcc.smartcity.fontui.FontEditText
import com.gcc.smartcity.userregistartion.controller.RegistrationController
import com.gcc.smartcity.userregistartion.model.userNameCheckModel
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.text.SimpleDateFormat
import java.util.*


class SignUpActivity : BaseActivity() {

    private var mRegistrationController: RegistrationController? = null
    private var dob: FontEditText? = null
    private var email: FontEditText? = null
    private var isEmailValid: Boolean = false
    private var isUserNameValid: Boolean = false
    private var password: FontEditText? = null
    private var username: FontEditText? = null
    private var isPasswordStrengthValid: Boolean = false
    private var name: FontEditText? = null
    private val myCalendar: Calendar = Calendar.getInstance()

    init {
        mRegistrationController = RegistrationController(this)
    }

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
            if (name?.text.toString().isNotEmpty() && (isEmailValid && email?.text.toString().isNotEmpty()) && (isUserNameValid && username?.text.toString().isNotEmpty()) && (isPasswordStrengthValid && password?.text.toString().isNotEmpty()) && dob?.text.toString().isNotEmpty()) {
                val intent = Intent(this, OTPVerifyActivity::class.java)
                intent.putExtra("name", name?.text.toString())
                intent.putExtra("email", email?.text.toString())
                intent.putExtra("password", password?.text.toString())
                intent.putExtra("dob", dob?.text.toString())
                intent.putExtra("username", username?.text.toString())
                startActivity(intent)
//            setContentView(R.layout.activity_select_avatar)
//            selectAvatar()
            } else {
                showErrorDialog(
                    getString(R.string.insufficientDetails),
                    getString(R.string.incorrectSignUpDetails),
                    getString(R.string.okButtonText)
                )
            }
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
                validateUserNameCall(username?.text.toString())
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

    private fun validateUserNameCall(userName: String) {
        mRegistrationController?.checkUserNameExistsCall(
            BuildConfig.HOST + java.lang.String.format(
                "users/%s/availability",
                userName
            )
        )
            ?.continueWithTask { task ->
                validateUserName(task)
            }
    }

    private fun validateUserName(task: Task<Any>): Task<Any>? {
        if (task.isFaulted) {
            username?.setBackgroundResource(R.drawable.bg_border_edittext_wrong)
            task.makeVoid()
        } else {
            val userNameModel = task.result as userNameCheckModel
            if (userNameModel.success!!) {
                username?.setBackgroundResource(R.drawable.bg_border_edittext)
                isUserNameValid = true
            } else {
                username?.setBackgroundResource(R.drawable.bg_border_edittext_wrong)
                showErrorDialog(
                    getString(R.string.usernameUnavailable),
                    getString(R.string.usernameUnavailableMessage),
                    getString(R.string.okButtonText)
                )
                isUserNameValid = false
            }
        }

        return null
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
        val myFormat = "dd-MM-yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        dob?.setText(sdf.format(myCalendar.time))
    }

    private fun showToast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
        toast.show()
    }
}

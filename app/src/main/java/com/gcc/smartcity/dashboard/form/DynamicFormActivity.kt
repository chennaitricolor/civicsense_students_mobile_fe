package com.gcc.smartcity.dashboard.form

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.gcc.smartcity.R
import com.gcc.smartcity.dashboard.model.DynamicFormData
import com.gcc.smartcity.dashboard.model.DynamicFormUserInputData
import com.gcc.smartcity.fontui.FontEditText
import com.gcc.smartcity.fontui.FontTextView
import com.gcc.smartcity.utils.AlertDialogBuilder
import com.gcc.smartcity.utils.ApplicationConstants
import com.gcc.smartcity.utils.Logger
import kotlinx.android.synthetic.main.activity_dynamic_form.*


class DynamicFormActivity : AppCompatActivity() {

    lateinit var dataList: ArrayList<DynamicFormData>
    lateinit var userInputArray: ArrayList<DynamicFormUserInputData>
    lateinit var editTextList: ArrayList<EditText>
    lateinit var spinnerList: ArrayList<Spinner>

    var isDialog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_form)
        userInputArray = ArrayList()
        dataList = ArrayList()
        editTextList = ArrayList()
        spinnerList = ArrayList()
        loadData()
        loadLayout()
        btn_basicform_Next.setOnClickListener { parseData() }

        btn_basicform_cancel.setOnClickListener { finish() }
    }

    private fun loadData() {

        val dynamicForm = DynamicFormData("Name", "String", true, null)
        dataList.add(dynamicForm)
        val dynamicForm1 = DynamicFormData("Age", "Number", false, null)
        dataList.add(dynamicForm1)
        val dynamicForm2 = DynamicFormData("Phone no", "Number", true, null)
        dataList.add(dynamicForm2)
        val dynamicForm3 = DynamicFormData("Address", "String", true, null)
        dataList.add(dynamicForm3)

        val genderArray = ArrayList<String>()
        genderArray.add("Male")
        genderArray.add("Female")
        genderArray.add("Trans-gender")
        val dynamicForm4 = DynamicFormData("Gender", "dropdown", true, genderArray)
        dataList.add(dynamicForm4)

        val zoneArray = ArrayList<String>()
        zoneArray.add("Alandur")
        zoneArray.add("AnnaNagar")
        zoneArray.add("Adayar")
        val dynamicForm5 = DynamicFormData("Zone", "dropdown", true, zoneArray)
        dataList.add(dynamicForm5)
    }

    @SuppressLint("ResourceType")
    private fun loadLayout() {
        val mContainerView = findViewById<View>(R.id.lin_basicform_dynamic) as LinearLayout
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        for (i in 0 until dataList.size) {
            val myView = inflater.inflate(R.layout.layout_dynamic_form, null)
            val labelName = myView.findViewById<FontTextView>(R.id.txt_basicform_title)
            val editText = myView.findViewById<FontEditText>(R.id.edt_basicform_input)
            val requiredTxt = myView.findViewById<FontTextView>(R.id.txt_basicform_mandatory)
            val dropDown = myView.findViewById<Spinner>(R.id.spn_basicform_dropdown)

            labelName.setText(dataList[i].labelName)

            inflateInputField(editText, dropDown, requiredTxt, i, dataList[i], dataList[i].data)

            mContainerView.addView(myView)
        }

    }

    fun isEditText(type: String): Boolean {
        return (ApplicationConstants.INPUTTYPE_NUMBER == type || ApplicationConstants.INPUTTYPE_String == type)

    }

    private fun inflateInputField(
        editText: EditText,
        dropDown: Spinner,
        required: TextView,
        i: Int,
        data: DynamicFormData,
        dropdownData: ArrayList<String>?
    ) {
        if (isEditText(data.type)) {
            setupEdittext(editText, required, dropDown, i, data)
        } else {
            setupDropdown(editText, required, dropDown, i, data, dropdownData)
        }
    }

    private fun setupEdittext(
        editText: EditText,
        required: TextView,
        dropDown: Spinner,
        i: Int,
        data: DynamicFormData
    ) {
        dropDown.visibility = View.GONE
        editText.visibility = View.VISIBLE
        if (data.type == "Number") {
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT
        }
        val editTextId = ApplicationConstants.editTextId + i
        editText.id = editTextId
        editTextList.add(editText)
        userInputArray.add(
            DynamicFormUserInputData(
                editTextId,
                "" + data.labelName,
                dataList[i].reqiured,
                true,
                editText,
                dropDown
            )
        )

        if (data.reqiured) {
            required.visibility = View.VISIBLE
        } else {
            required.visibility = View.INVISIBLE
        }
    }

    private fun setupDropdown(
        editText: EditText,
        required: TextView,
        dropDown: Spinner,
        i: Int,
        data: DynamicFormData,
        dropdownData: ArrayList<String>?
    ) {
        dropDown.visibility = View.VISIBLE
        editText.visibility = View.GONE
        required.visibility = View.INVISIBLE
        val spinnerId = ApplicationConstants.spinnerId + i
        userInputArray.add(
            DynamicFormUserInputData(
                spinnerId,
                "" + data.labelName,
                false,
                false,
                editText,
                dropDown
            )
        )

        //Creating the ArrayAdapter instance having the country list
        val arrayAdapter = dropdownData?.toArray()?.let {
            ArrayAdapter(
                this, android.R.layout.simple_spinner_item, it
            )
        }
        arrayAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Setting the ArrayAdapter data on the Spinner
        dropDown.setAdapter(arrayAdapter)
        spinnerList.add(dropDown)
    }

    private fun parseData() {

        for (i in 0..userInputArray.size - 1) {
            val userData = userInputArray.get(i)
            if (userData.isEdittext) {
                val editText = userData.editText
                Logger.d("Edittext---"+userData.label+"=" + editText.text.toString())
                if (userData.reqiured) {
                    if (editText.text.toString().trim().length == 0) {
                        isDialog = true
                        break
                    }
                }
            } else {
                val spinner = userData.spinner
                Logger.d("Spinner data---" +userData.label+"="+ spinner.selectedItem.toString())
            }
        }

        if (isDialog) {
            AlertDialogBuilder.getInstance()
                .showErrorDialog("Warning!!", "Please fill *required fields", "OK", this)
        } else {
            Toast.makeText(this, "Good to go", Toast.LENGTH_SHORT).show()
        }
    }
}
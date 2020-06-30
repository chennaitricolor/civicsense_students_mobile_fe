package com.gcc.smartcity.dashboard.form

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.gcc.smartcity.R
import com.gcc.smartcity.dashboard.ImageCaptureActivity
import com.gcc.smartcity.dashboard.model.DynamicFormData
import com.gcc.smartcity.dashboard.model.DynamicFormUserInputData
import com.gcc.smartcity.dashboard.model.NewMissionListModel
import com.gcc.smartcity.fontui.FontEditText
import com.gcc.smartcity.fontui.FontTextView
import com.gcc.smartcity.preference.SessionStorage
import com.gcc.smartcity.utils.AlertDialogBuilder
import com.gcc.smartcity.utils.AnimationUtil
import com.gcc.smartcity.utils.ApplicationConstants
import com.gcc.smartcity.utils.Logger
import kotlinx.android.synthetic.main.activity_dynamic_form.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class DynamicFormActivity : AppCompatActivity() {

    private lateinit var dataList: ArrayList<DynamicFormData>
    private lateinit var userInputArray: ArrayList<DynamicFormUserInputData>
    private lateinit var editTextList: ArrayList<EditText>
    private lateinit var spinnerList: ArrayList<Spinner>
    private var newMissionListModel: NewMissionListModel? = null

    var isDialog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_form)

        newMissionListModel = SessionStorage.getInstance().newMissionListModel

        userInputArray = ArrayList()
        dataList = ArrayList()
        editTextList = ArrayList()
        spinnerList = ArrayList()

        if (newMissionListModel != null) {
            loadData(newMissionListModel!!)
            loadLayout()
        }

        AnimationUtil.buttonEffect(btn_basicform_Next, "#d4993d")
        AnimationUtil.buttonEffect(btn_basicform_cancel, "#7aa133")
        btn_basicform_Next.setOnClickListener { parseData() }
        btn_basicform_cancel.setOnClickListener { finish() }
    }

    private fun loadData(newMissionListModel: NewMissionListModel) {
        val formFields = newMissionListModel.task?.formFields
        for (i in 0 until formFields?.size!!) {
            if (formFields[i]?.type.toString().toLowerCase(Locale.getDefault()) == "dropdown") {
                val spinnerArray = ArrayList<String>()
                for (x in 0 until formFields[i]?.data?.size!!) {
                    spinnerArray.add(formFields[i]?.data?.get(x).toString())
                }
                val dynamicForm4 = DynamicFormData(
                    formFields[i]?.label.toString(),
                    "dropdown",
                    formFields[i]?.required!!,
                    spinnerArray
                )
                dataList.add(dynamicForm4)
            } else {
                val dynamicForm = DynamicFormData(
                    formFields[i]?.label.toString(),
                    formFields[i]?.type.toString(),
                    formFields[i]?.required!!,
                    null
                )
                dataList.add(dynamicForm)
            }
        }

//        val dynamicForm = DynamicFormData("Name", "String", true, null)
//        dataList.add(dynamicForm)
//        val dynamicForm1 = DynamicFormData("Age", "Number", false, null)
//        dataList.add(dynamicForm1)
//        val dynamicForm2 = DynamicFormData("Phone no", "Number", true, null)
//        dataList.add(dynamicForm2)
//        val dynamicForm3 = DynamicFormData("Address", "String", true, null)
//        dataList.add(dynamicForm3)

//        val genderArray = ArrayList<String>()
//        genderArray.add("Male")
//        genderArray.add("Female")
//        genderArray.add("Transgender")
//        val dynamicForm4 = DynamicFormData("Gender", "dropdown", true, genderArray)
//        dataList.add(dynamicForm4)
//
//        val zoneArray = ArrayList<String>()
//        zoneArray.add("Alandur")
//        zoneArray.add("AnnaNagar")
//        zoneArray.add("Adayar")
//        val dynamicForm5 = DynamicFormData("Zone", "dropdown", true, zoneArray)
//        dataList.add(dynamicForm5)
    }

    private fun loadLayout() {
        val mContainerView = findViewById<View>(R.id.lin_basicform_dynamic) as LinearLayout
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        for (i in 0 until dataList.size) {
            val myView = inflater.inflate(R.layout.layout_dynamic_form, null)
            val labelName = myView.findViewById<FontTextView>(R.id.txt_basicform_title)
            val editText = myView.findViewById<FontEditText>(R.id.edt_basicform_input)
            val requiredTxt = myView.findViewById<FontTextView>(R.id.txt_basicform_mandatory)
            val dropDown = myView.findViewById<Spinner>(R.id.spn_basicform_dropdown)

            labelName.text = dataList[i].labelName

            inflateInputField(editText, dropDown, requiredTxt, i, dataList[i], dataList[i].data)

            mContainerView.addView(myView)
        }

    }

    private fun isEditText(type: String): Boolean {
        return (ApplicationConstants.INPUTTYPE_NUMBER.toLowerCase(Locale.getDefault()) == type || ApplicationConstants.INPUTTYPE_String.toLowerCase(Locale.getDefault()) == type || ApplicationConstants.INPUTTYPE_Integer.toLowerCase(Locale.getDefault()) == type)
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
            setupEditText(editText, required, dropDown, i, data)
        } else {
            setupDropdown(editText, required, dropDown, i, data, dropdownData)
        }
    }

    private fun setupEditText(
        editText: EditText,
        required: TextView,
        dropDown: Spinner,
        i: Int,
        data: DynamicFormData
    ) {
        dropDown.visibility = View.GONE
        editText.visibility = View.VISIBLE
        if (data.type.toLowerCase(Locale.getDefault()) == "number" || data.type.toLowerCase(Locale.getDefault()) == "integer") {
            editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
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
                dataList[i].required,
                true,
                editText,
                dropDown
            )
        )

        if (data.required) {
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

        val arrayAdapter = dropdownData?.toArray()?.let {
            ArrayAdapter(
                this, android.R.layout.simple_spinner_item, it
            )
        }
        arrayAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dropDown.adapter = arrayAdapter
        spinnerList.add(dropDown)
    }

    private fun parseData() {
        val hashMap: HashMap<String, String> = HashMap()

        for (i in 0 until userInputArray.size) {
            val userData = userInputArray[i]
            if (userData.isEditText) {
                val editText = userData.editText
                Logger.d("Edittext---" + userData.label + "=" + editText.text.toString())
                hashMap[userData.label] = editText.text.toString()
                if (userData.required) {
                    if (editText.text.toString().trim().isEmpty()) {
                        isDialog = true
                        break
                    } else {
                        isDialog = false
                    }
                }
            } else {
                val spinner = userData.spinner
                Logger.d("Spinner data---" + userData.label + "=" + spinner.selectedItem.toString())
                hashMap[userData.label] = spinner.selectedItem.toString()
            }
        }

        if (isDialog) {
            AlertDialogBuilder.getInstance()
                .showErrorDialog(
                    "Warning!",
                    "Please fill all the required fields marked with a '*'",
                    "OK",
                    this
                )
        } else {
            openImageCaptureActivity(hashMap)
        }
    }

    private fun openImageCaptureActivity(hashMap: HashMap<String, String>) {
        val intent = Intent(this, ImageCaptureActivity::class.java)
        intent.putExtra("_id", newMissionListModel?.task?._id)
        intent.putExtra("_campaignName", newMissionListModel?.task?.campaignName)
        intent.putExtra("rewards", newMissionListModel?.task?.rewards)
        intent.putExtra("formValues", hashMap)
        intent.putExtra("fromScreen", "dynamicFormActivity")
        startActivity(intent)
        finish()
    }

}
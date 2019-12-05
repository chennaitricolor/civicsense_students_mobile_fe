package com.gcc.smartcity.dashboard

import android.Manifest.permission.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.gcc.smartcity.R
import com.gcc.smartcity.SubmitActivity
import com.gcc.smartcity.utils.AlertDialogBuilder
import com.gcc.smartcity.utils.OnDialogListener
import kotlinx.android.synthetic.main.activity_image_capture.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ImageCaptureActivity : AppCompatActivity(), OnDialogListener {

    private val PERMISSION_REQUEST_CODE = 200
    private val CAMERA_REQUEST_CODE = 1450
    private val SETTINGS_REQUEST_CODE = 1111
    private var ivCameraPreview: ImageView? = null
    private var buttonHolder: LinearLayout? = null
    private var mCurrentPhotoPath: String? = null
    private var reTakeButton: Button? = null
    private var submitButton: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initiateImageGrab()

        setContentView(R.layout.activity_image_capture)

        ivCameraPreview = findViewById(R.id.ivCameraPreview)
        buttonHolder = findViewById(R.id.buttonHolder)
        reTakeButton = findViewById(R.id.btnReTakePhoto)
        submitButton = findViewById(R.id.btnSubmitPhoto)

        setButtonHolderVisibility(false)

        buttonEffect(btnReTakePhoto, "#d4993d")
        buttonEffect(btnSubmitPhoto, "#7aa133")

        reTakeButton?.setOnClickListener {
            initiateImageGrab()
        }

        submitButton?.setOnClickListener {
            if (checkPermission()) {
                if (isLocationEnabled()) {
                    val intent = Intent(this, SubmitActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Please turn on LOCATION",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                requestPermission()
            }
        }
    }

    private fun buttonEffect(button: View, color: String) {
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.background.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_ATOP)
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

    private fun isLocationEnabled(): Boolean {
        val isGpsEnabled: Boolean
        val isNetworkEnabled: Boolean

        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        Log.i("LocationStatus", "Location is: $isGpsEnabled")

        return isGpsEnabled && isNetworkEnabled
    }

    private fun setButtonHolderVisibility(show: Boolean) {
        if (show) {
            buttonHolder?.visibility = View.VISIBLE
        } else {
            buttonHolder?.visibility = View.INVISIBLE
        }
    }

    private fun initiateImageGrab() {
        if (checkPermission()) {
            launchCamera()
        } else {
            requestPermission()
        }
    }

    private fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            this,
            CAMERA
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        val perms = arrayOf(
            CAMERA,
            WRITE_EXTERNAL_STORAGE,
            ACCESS_FINE_LOCATION
        )
        ActivityCompat.requestPermissions(this, perms, PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty()) {
                val cameraAccepted =
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                val writeFileAccepted =
                    grantResults[1] == PackageManager.PERMISSION_GRANTED
                val locationAccepted =
                    grantResults[2] == PackageManager.PERMISSION_GRANTED
                if (cameraAccepted && writeFileAccepted && locationAccepted) {
                    launchCamera()
                } else {
                    Toast.makeText(applicationContext, "Failed permissions", Toast.LENGTH_SHORT)
                        .show()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                            AlertDialogBuilder.getInstance().showErrorDialog(
                                "Permissions have been denied",
                                "We need access to all the permissions in order to capture the image and upload it to our servers to serve you better.",
                                "CANCEL",
                                "OK",
                                "rationalePermissionRequest",
                                this,
                                this
                            )
                            return
                        } else {
                            AlertDialogBuilder.getInstance().showErrorDialog(
                                "Permissions Required",
                                "It seems you have disabled the required permissions permanently. The app will not work without those permissions. If you would like to grant the permissions, click on 'OK' and grant all permissions. If you wish not to, the app will shutdown itself.",
                                "CANCEL",
                                "OK",
                                "forcePermissionRequest",
                                this,
                                this
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Launches the default camera application
     */
    private fun launchCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create the File where the photo should go
        var photoFile: File? = null
        try {
            photoFile = createImageFile()
        } catch (ex: IOException) { // Error occurred while creating the File
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.gcc.smartcity",
                photoFile
            )
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            //Start the camera application
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_REQUEST_CODE) {
            initiateImageGrab()
        } else if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == 0) {
                val intent = Intent(this, DashBoardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            } else {
                val bmOptions = BitmapFactory.Options()
                bmOptions.inSampleSize = 4
                val bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions)
                ivCameraPreview!!.setImageBitmap(bitmap)
                setButtonHolderVisibility(true)
            }
        }
    }

    /**
     * Creates the image file in the external directory
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun createImageFile(): File? { // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image: File = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    override fun onPositiveButtonClick(whichDialog: String?) {
        if (whichDialog == "rationalePermissionRequest") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(
                        WRITE_EXTERNAL_STORAGE,
                        CAMERA,
                        ACCESS_FINE_LOCATION
                    ),
                    PERMISSION_REQUEST_CODE
                )
            }
        } else if (whichDialog == "forcePermissionRequest") {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivityForResult(intent, SETTINGS_REQUEST_CODE)
        }
    }

    override fun onNegativeButtonClick(whichDialog: String?) {
        if (whichDialog == "rationalePermissionRequest") {
            finish()
        } else if (whichDialog == "forcePermissionRequest") {
            finish()
        }
    }


}


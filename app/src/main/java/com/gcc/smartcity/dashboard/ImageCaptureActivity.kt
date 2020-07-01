package com.gcc.smartcity.dashboard

import android.Manifest.permission.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.gcc.smartcity.BuildConfig
import com.gcc.smartcity.FileUpload
import com.gcc.smartcity.R
import com.gcc.smartcity.network.PersistentCookieStore
import com.gcc.smartcity.submit.SubmitActivityAgentX
import com.gcc.smartcity.submit.SubmitActivityCorona
import com.gcc.smartcity.utils.AlertDialogBuilder
import com.gcc.smartcity.utils.OnDialogListener
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_image_capture.*
import java.io.File
import java.io.IOException
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.text.SimpleDateFormat
import java.util.*

class ImageCaptureActivity : AppCompatActivity(), OnDialogListener, ImageUploadListener {
    override fun onSuccess() {
        if (BuildConfig.APPNAME == "AGENTX") {
            val intent = Intent(this, SubmitActivityAgentX::class.java)
            intent.putExtra("rewards", rewards)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, SubmitActivityCorona::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG)
            .show()
        finish()
    }

    private val PERMISSION_REQUEST_CODE = 200
    private val CAMERA_REQUEST_CODE = 1450
    private val SETTINGS_REQUEST_CODE = 1111
    private var ivCameraPreview: ImageView? = null
    private var buttonHolder: LinearLayout? = null
    private var imageCaptureActivity: RelativeLayout? = null
    private var mCurrentPhotoPath: String? = null
    private var reTakeButton: Button? = null
    private var submitButton: Button? = null
    private var bitmap: Bitmap? = null
    private var _id: String? = null
    private var _campaignName: String? = null
    private var rewards: String? = null
    private var hashMap: HashMap<String, String>? = null
    private val PERMISSION_ID = 42
    private var mLatitude: String? = null
    private var mLongitude: String? = null
    private lateinit var lastLocation: Location
    private lateinit var locationCallback: LocationCallback
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation
                mLatitude = lastLocation.latitude.toString()
                mLongitude = lastLocation.longitude.toString()
            }
        }

        getLastLocation()

        if (intent.extras != null) {
            _id = intent.extras!!.getString("_id").toString()
            _campaignName = intent.extras!!.getString("_campaignName").toString()
            rewards = intent.extras!!.getString("rewards").toString()
            if (intent.extras!!.getString("fromScreen").toString() == "dynamicFormActivity") {
                hashMap = intent.getSerializableExtra("formValues") as? HashMap<String, String>
            }
        }

        initiateImageGrab()

        setContentView(R.layout.activity_image_capture)

        ivCameraPreview = findViewById(R.id.ivCameraPreview)
        buttonHolder = findViewById(R.id.buttonHolder)
        reTakeButton = findViewById(R.id.btnReTakePhoto)
        submitButton = findViewById(R.id.btnSubmitPhoto)
        imageCaptureActivity = findViewById(R.id.imageCaptureActivity)

        setButtonHolderVisibility(false)

        buttonEffect(btnReTakePhoto, "#d4993d")
        buttonEffect(btnSubmitPhoto, "#7aa133")

        reTakeButton?.setOnClickListener {
            initiateImageGrab()
        }

        submitButton?.setOnClickListener {
            if (checkPermission()) {
                if (isLocationEnabled()) {
                    uploadFileToServer()
                } else {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.turnOnLocationMessage),
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                requestPermission()
            }
        }
    }

    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabledPermissionCheck()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        Log.d("Old Latitude", location.latitude.toString())
                        Log.d("Old Longitude", location.longitude.toString())
                        mLatitude = location.latitude.toString()
                        mLongitude = location.longitude.toString()
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.turnOnLocationMessage), Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun requestNewLocationData() {
        mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            Log.d("New Latitude", locationResult.lastLocation.latitude.toString())
            Log.d("New Longitude", locationResult.lastLocation.longitude.toString())
            mLatitude = locationResult.lastLocation.latitude.toString()
            mLongitude = locationResult.lastLocation.longitude.toString()
        }
    }

    private fun isLocationEnabledPermissionCheck(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    private fun uploadFileToServer() {
        val cookieManager = CookieManager(
            PersistentCookieStore(), CookiePolicy.ACCEPT_ORIGINAL_SERVER
        )
        CookieHandler.setDefault(cookieManager)

        Thread(Runnable {
            FileUpload(this, this).uploadScreenshotCall(
                true,
                mLatitude,
                mLongitude,
                BuildConfig.HOST + "user/task",
                bitmap,
                "image/jpeg",
                _id,
                _campaignName,
                hashMap
            )
        }).start()

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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                            AlertDialogBuilder.getInstance().showErrorDialog(
                                getString(R.string.permissionsDeniedTitle),
                                getString(R.string.permissionRequestMessage),
                                getString(R.string.cancelButtonText),
                                getString(R.string.okButtonText),
                                "rationalePermissionRequest",
                                this,
                                this
                            )
                            return
                        } else {
                            AlertDialogBuilder.getInstance().showErrorDialog(
                                getString(R.string.permissionsRequired),
                                getString(R.string.permanentPermissionDeniedMessage),
                                getString(R.string.cancelButtonText),
                                getString(R.string.okButtonText),
                                "forcePermissionRequest",
                                this,
                                this
                            )
                        }
                    }
                }
            }
            PERMISSION_ID -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getLastLocation()
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
                BuildConfig.APPLICATION_ID + ".provider",
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
                bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions)

                val ei = ExifInterface(mCurrentPhotoPath!!)
                val orientation: Int = ei.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )
                val rotatedBitmap: Bitmap?
                rotatedBitmap = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90F)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180F)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270F)
                    ExifInterface.ORIENTATION_NORMAL -> bitmap
                    else -> bitmap
                }

                ivCameraPreview!!.setImageBitmap(rotatedBitmap)
                setButtonHolderVisibility(true)
            }
        }
    }

    private fun rotateImage(source: Bitmap?, angle: Float): Bitmap {
        val matrix: Matrix? = Matrix()
        matrix?.postRotate(angle)
        return Bitmap.createBitmap(
            source!!, 0, 0, source.width, source.height,
            matrix, true
        )
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


package rachman.forniandi.circlegathering.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.databinding.ActivityFormAddDataBinding
import rachman.forniandi.circlegathering.databinding.CustomDialogImageSelectionBinding
import rachman.forniandi.circlegathering.utils.NetworkResult
import rachman.forniandi.circlegathering.utils.animateLoadingProcessData
import rachman.forniandi.circlegathering.utils.createCustomTempFileImg
import rachman.forniandi.circlegathering.utils.rotateBitmap
import rachman.forniandi.circlegathering.utils.uriImgToFileImg
import rachman.forniandi.circlegathering.viewModels.UploadViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@ExperimentalPagingApi
@AndroidEntryPoint
class FormAddDataActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding:ActivityFormAddDataBinding
    private val viewModel: UploadViewModel by viewModels()
    private var inputFile: File? = null
    private var compressedFile: File? = null
    private var mImgPath:String =""
    private var location: Location? = null
    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    private lateinit var descriptionToRequestBody: RequestBody
    private lateinit var fileBodyMultipart : MultipartBody.Part

    private var mLatitude: Double ? = null
    private var mLongitude: Double = 0.0

    companion object {
        const val CAMERA_X_RESULT = 200


        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!cameraPermissionGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.not_get_permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun cameraPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormAddDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!cameraPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        binding.imgAddInput.setOnClickListener(this)
        binding.btnUploadDataConfirm.setOnClickListener(this)
        binding.btnSwitchGps.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                getLatestLocation()
            }else{
                this.location = null
            }
        }

        //setSupportActionBar(binding.toolbarAddData)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

    }

    @SuppressLint("SetTextI18n")
    private fun getLatestLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener { mLocation ->
                if (mLocation != null){
                    this.location = mLocation
                    mLatitude = mLocation.latitude
                    mLongitude = mLocation.longitude
                    binding.txtValueLatitude.text= mLatitude.toString()
                    binding.txtValueLongitude.text= mLongitude.toString()
                }else{
                    Toast.makeText(this, "Please activate your location services",
                        Toast.LENGTH_SHORT).show()
                    binding.btnSwitchGps.isChecked= false
                }
            }
        }else{
            requestPermissionLocationLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val requestPermissionLocationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getLatestLocation()
            }

            else -> {
                Snackbar
                    .make(
                        binding.root,
                        getString(R.string.location_permission_denied),
                        Snackbar.LENGTH_SHORT
                    )
                    .setActionTextColor(getColor(R.color.white))
                    .setAction(getString(R.string.change_setting)) {

                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also { intent ->
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri

                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    .show()

                binding.btnSwitchGps.isChecked = false
            }
        }
    }

    override fun onClick(view: View?) {
        if (view != null){
            when(view.id){
                R.id.img_add_input->{
                    customOptionImageInputSelectionDialog()
                    return
                }

                R.id.btn_upload_data_confirm->{
                    if (inputFile == null){
                        Snackbar.make(binding.root, getString(R.string.insert_input_upload_validation_message),
                            Snackbar.LENGTH_SHORT).show()
                    }else{
                        val description =binding.etDescription.text.toString().trim()
                        val reduceImageFirst = actionReduceImg(inputFile as File)
                        executeUploadData(inputFile as File,description,mLatitude,mLongitude)
                        //executeUploadData(reduceImageFirst,description,mLatitude,mLongitude)
                    }
                    return
                }
            }
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun executeUploadData(reduceImageFirst: File, description: String,latitude:Double?,longitude:Double?) {
        lifecycleScope.launch {
                val requestBodyInput = reduceImageFirst.asRequestBody("file_img/jpeg".toMediaType())
                fileBodyMultipart = MultipartBody.Part.createFormData("photo", reduceImageFirst.name, requestBodyInput)

                descriptionToRequestBody = description.toRequestBody("text/plain".toMediaType())

            var lat: RequestBody? = null
            var lon: RequestBody? = null

            if (location != null) {
                lat =
                    latitude.toString().toRequestBody("text/plain".toMediaType())
                lon =
                    longitude.toString().toRequestBody("text/plain".toMediaType())
            }

            viewModel.doUploadStoriesData(fileBodyMultipart,descriptionToRequestBody,lat, lon)

                Log.e("test_input_upload_file",""+ fileBodyMultipart)
                viewModel.inputDataResponse.observe(this@FormAddDataActivity) { response ->
                    when (response) {
                        is NetworkResult.Loading -> {
                            applyLoadProgressStateUpload(true)
                        }

                        is NetworkResult.Success -> {
                            applyLoadProgressStateUpload(false)
                            Snackbar.make(
                                binding.root,
                                response.message.toString(), Snackbar.LENGTH_SHORT).show()
                            startActivity(Intent(this@FormAddDataActivity, MainActivity::class.java))
                            finish()
                        }

                        is NetworkResult.Error -> {
                            applyLoadProgressStateUpload(false)
                            Snackbar.make(
                                binding.root,
                                response.message.toString(), Snackbar.LENGTH_SHORT).show()
                        }
                    }
                    Log.e("test_response_view_upload",""+viewModel.inputDataResponse)
                }
        }
    }
    private fun actionReduceImg(insertImg: File): File {
        val decodeToBitmap = BitmapFactory.decodeFile(insertImg.path)

        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            decodeToBitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)

        decodeToBitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(insertImg))

        return insertImg
    }
    private fun customOptionImageInputSelectionDialog(){
        val optionTakeImageDialog= Dialog(this)
        val binding: CustomDialogImageSelectionBinding = CustomDialogImageSelectionBinding.inflate(layoutInflater)
        optionTakeImageDialog.setContentView(binding.root)

        binding.txtOptionCamera.setOnClickListener {
            takeActionCamera()
            optionTakeImageDialog.dismiss()
        }
        binding.txtOptionCameraX.setOnClickListener {
            takeActionCameraX()
            optionTakeImageDialog.dismiss()
        }

        binding.txtOptionGallery.setOnClickListener {
            takeActionGallery()
            optionTakeImageDialog.dismiss()
        }
        optionTakeImageDialog.show()
    }

    private fun takeActionCameraX() {
        val intent = Intent(this, CameraXActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun takeActionCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFileImg(application).also {
            val photoURICamera: Uri = FileProvider.getUriForFile(
                this@FormAddDataActivity,
                packageName,
                it
            )
            mImgPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURICamera)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun takeActionGallery() {
        val galleryIntent = Intent()
        galleryIntent.action= Intent.ACTION_GET_CONTENT
        galleryIntent.type = "image/*"
        val pickImg = Intent.createChooser(galleryIntent, "Choose a Picture")
        launcherIntentGallery.launch(pickImg)
    }


    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {result->
        lifecycleScope.launch {
            if (result.resultCode == RESULT_OK) {
                val filePicCamera = File(mImgPath)
                inputFile= filePicCamera

                compressedFile = Compressor.compress(this@FormAddDataActivity, inputFile!!)

                val resultCamera = BitmapFactory.decodeFile(compressedFile?.path)

                binding.imgDisplayInput.setImageBitmap(resultCamera)

                Log.i("imgPath", mImgPath)

                binding.imgAddInput.setImageDrawable(ContextCompat.getDrawable(this@FormAddDataActivity,R.drawable.ic_vector_edit))
            }else if (result.resultCode == Activity.RESULT_CANCELED){
                Log.e("Canceled","Canceled")
            }
        }

    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result->
        lifecycleScope.launch {
            if (result.resultCode == CAMERA_X_RESULT){
                val getFileFromCamX = result.data?.getSerializableExtra("pictCameraX")as File
                val getImgOrientation= result.data?.getBooleanExtra("isBackCameraX",true)as Boolean
                inputFile = getFileFromCamX

                compressedFile=Compressor.compress(this@FormAddDataActivity, inputFile!!)

                val resultCameraX= rotateBitmap(
                    BitmapFactory.decodeFile(compressedFile?.path),
                    getImgOrientation
                )
                binding.imgDisplayInput.setImageBitmap(resultCameraX)
            }
        }

    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result->
        if (result.resultCode == RESULT_OK) {
            val selectedPhotoUri= result?.data?.data as Uri

            val filePicGallery = uriImgToFileImg(selectedPhotoUri,this@FormAddDataActivity)
            inputFile = actionReduceImg(filePicGallery)


            binding.imgDisplayInput.setImageURI(selectedPhotoUri)

            binding.imgAddInput.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_vector_edit))
        } else if (result.resultCode == Activity.RESULT_CANCELED){
            Log.e("Canceled","Canceled")}
    }

    private fun applyLoadProgressStateUpload(onProcess:Boolean){
        binding.apply {
            etDescription.isEnabled = !onProcess
            imgAddInput.isEnabled = !onProcess
            imgDisplayInput.isEnabled = !onProcess
            btnUploadDataConfirm.isEnabled =!onProcess
        }

        if (onProcess){
            binding.maskedViewPgUpload.animateLoadingProcessData(true)
        }else{
            binding.maskedViewPgUpload.animateLoadingProcessData(false)
        }
    }

}





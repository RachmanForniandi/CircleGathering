package rachman.forniandi.circlegathering.activities

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
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


@AndroidEntryPoint
class FormAddDataActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding:ActivityFormAddDataBinding
    private val viewModel: UploadViewModel by viewModels()
    private var inputFile: File? = null
    private var mImgPath:String =""
    private lateinit var descriptionToRequestBody: RequestBody
    private lateinit var fileBodyMultipart : MultipartBody.Part

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
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormAddDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        binding.imgAddInput.setOnClickListener(this)
        binding.btnUploadDataConfirm.setOnClickListener(this)

        setSupportActionBar(binding.toolbarAddData)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

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
                        executeUploadData(reduceImageFirst,description)
                    }
                    return
                }
            }
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun executeUploadData(reduceImageFirst: File, description: String) {
        lifecycleScope.launch {
                val requestBodyInput = reduceImageFirst.asRequestBody("file_img/jpeg".toMediaType())
                fileBodyMultipart = MultipartBody.Part.createFormData("photo", reduceImageFirst.name, requestBodyInput)

                descriptionToRequestBody = description.toRequestBody("text/plain".toMediaType())

                viewModel.doUploadStoriesData(fileBodyMultipart,descriptionToRequestBody)

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
        if (result.resultCode == RESULT_OK) {
            val filePicCamera = File(mImgPath)
            inputFile= filePicCamera
            val resultCamera = BitmapFactory.decodeFile(inputFile?.path)
            binding.imgDisplayInput.setImageBitmap(resultCamera)

            Log.i("imgPath", mImgPath)

            binding.imgAddInput.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_vector_edit))
        }else if (result.resultCode == Activity.RESULT_CANCELED){
            Log.e("Canceled","Canceled")
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result->
        if (result.resultCode == CAMERA_X_RESULT){
            val getFileFromCamX = result.data?.getSerializableExtra("pictCameraX")as File
            val getImgOrientation= result.data?.getBooleanExtra("isBackCameraX",true)as Boolean
            //mImgPath= result.data?.getStringExtra(CameraXActivity.EXTRA_CAMERAX_IMAGE)!!
            inputFile = getFileFromCamX

            val resultCameraX= rotateBitmap(
                BitmapFactory.decodeFile(inputFile?.path),
                getImgOrientation
            )
            binding.imgDisplayInput.setImageBitmap(resultCameraX)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result->
        if (result.resultCode == RESULT_OK) {
            val selectedPhotoUri= result?.data?.data as Uri

            val filePicGallery = uriImgToFileImg(selectedPhotoUri,this@FormAddDataActivity)
            inputFile = filePicGallery
            binding.imgDisplayInput.setImageURI(selectedPhotoUri)

            binding.imgAddInput.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_vector_edit))
        } else if (result.resultCode == Activity.RESULT_CANCELED){
            Log.e("Canceled","Canceled")}
    }

    private fun applyLoadProgressStateUpload(onProcess:Boolean){
        binding.etDescription.isEnabled = !onProcess
        binding.imgAddInput.isEnabled = !onProcess
        binding.imgDisplayInput.isEnabled = !onProcess

        if (onProcess){
            binding.maskedViewPgUpload.animateLoadingProcessData(true)
        }else{
            binding.maskedViewPgUpload.animateLoadingProcessData(false)
        }
    }

}





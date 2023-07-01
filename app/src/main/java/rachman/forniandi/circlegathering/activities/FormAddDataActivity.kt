package rachman.forniandi.circlegathering.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.databinding.ActivityFormAddDataBinding
import rachman.forniandi.circlegathering.databinding.CustomDialogImageSelectionBinding
import rachman.forniandi.circlegathering.utils.NetworkResult
import rachman.forniandi.circlegathering.utils.animateLoadingProcessData
import rachman.forniandi.circlegathering.viewModels.UploadViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID
@AndroidEntryPoint
class FormAddDataActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding:ActivityFormAddDataBinding
    private val viewModel: UploadViewModel by viewModels()
    private var mImgPath:String =""
    private lateinit var descriptionToRequestBody: RequestBody
    private lateinit var fileBodyMultipart : MultipartBody.Part


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormAddDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgAddInput.setOnClickListener(this)
        binding.btnUploadDataConfirm.setOnClickListener(this)

    }
    override fun onClick(view: View?) {
        if (view != null){
            when(view.id){
                R.id.img_add_input->{
                    customOptionImageInputSelectionDialog()
                    return
                }

                R.id.btn_upload_data_confirm->{
                    val description =binding.etDescription.text.toString().trim()
                    val insertImg = File(mImgPath)
                    if (description.isEmpty() && mImgPath.isEmpty()){
                        Snackbar.make(binding.root, getString(R.string.insert_input_upload_validation_message),
                            Snackbar.LENGTH_SHORT).show()
                    }else{
                        executeUploadData(insertImg,description)
                    }
                    return
                }
            }
        }
    }

    private fun executeUploadData(insertImg: File, description: String) {
        lifecycleScope.launch {
                val fileToCompressProcess = Compressor.compress(this@FormAddDataActivity, insertImg) {
                    quality(50)
                    size(1_000_000)
                }
                descriptionToRequestBody = description.toRequestBody("text/plain".toMediaType())

                val requestBodyInput = fileToCompressProcess.asRequestBody("image/jpeg".toMediaType())
                fileBodyMultipart = MultipartBody.Part.createFormData("image", insertImg.name, requestBodyInput)


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

    private fun customOptionImageInputSelectionDialog(){
        val optionTakeImageDialog= Dialog(this)
        val binding: CustomDialogImageSelectionBinding = CustomDialogImageSelectionBinding.inflate(layoutInflater)
        optionTakeImageDialog.setContentView(binding.root)

        binding.txtOptionCamera.setOnClickListener {
            Dexter.withContext(this)
                .withPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if (report.areAllPermissionsGranted()){
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                startActivityForResult(intent, CAMERA)
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?) {
                        showRationalDialogForPermissions()
                    }
                }).onSameThread()
                .check()
            optionTakeImageDialog.dismiss()
        }

        binding.txtOptionGallery.setOnClickListener {
            Dexter.withContext(this@FormAddDataActivity)
                .withPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        val galleryIntent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                        startActivityForResult(galleryIntent, GALLERY)
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        Toast.makeText(this@FormAddDataActivity,"You have denied the storage permission to select image.",
                            Toast.LENGTH_SHORT).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                        showRationalDialogForPermissions()
                    }
                }).onSameThread()
                .check()
            optionTakeImageDialog.dismiss()
        }
        optionTakeImageDialog.show()
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton("GO TO SETTINGS"){ _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package",packageName,null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel"){
                    dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){

            if (requestCode == CAMERA){
                data?.extras.let{
                    val thumbnail: Bitmap = data?.extras?.get("data") as Bitmap
                    Glide.with(this)
                        .load(thumbnail)
                        .centerCrop()
                        .into(binding.imgDisplayInput)

                    mImgPath = saveImageToInternalStorage(thumbnail)

                    Log.i("imgPath", mImgPath)

                    binding.imgAddInput.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_vector_edit))
                }
            }else if (requestCode == GALLERY){
                data?.extras.let{
                    val selectedPhotoUri= data?.data
                    binding.imgDisplayInput.setImageURI(selectedPhotoUri)

                    Glide.with(this)
                        .load(selectedPhotoUri)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Log.e("TAG","Error loading image",e)
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                resource?.let{
                                    val bitmap: Bitmap = resource.toBitmap()
                                    mImgPath = saveImageToInternalStorage(bitmap)
                                    Log.i("imagePath",mImgPath)
                                }
                                return false
                            }
                        })
                        .into(binding.imgDisplayInput)

                    binding.imgAddInput.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_vector_edit))
                }
            }else if (resultCode == Activity.RESULT_CANCELED){
                Snackbar.make(
                    binding.root,
                    getString(R.string.all_permission_device_are_denied), Snackbar.LENGTH_SHORT).show()
            }
        }
    }


    private fun saveImageToInternalStorage(thumbnail: Bitmap): String {
        val wrapper = ContextWrapper(applicationContext)

        var file = wrapper.getDir(IMG_DIRECTORY, Context.MODE_PRIVATE)

        file = File(file,"${UUID.randomUUID()}.jpg")

        try {
            val stream : OutputStream = FileOutputStream(file)
            thumbnail.compress(Bitmap.CompressFormat.JPEG,100, stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
        return file.absolutePath
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


    companion object {
        private const val CAMERA = 1
        private const val GALLERY = 2

        private const val IMG_DIRECTORY ="IMG_DIRECTORY "
    }




}





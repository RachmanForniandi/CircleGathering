package rachman.forniandi.circlegathering.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.OrientationEventListener
import android.view.Surface
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.*
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.FLASH_MODE_OFF
import androidx.camera.core.ImageCapture.FLASH_MODE_ON
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.databinding.ActivityCameraXBinding
import rachman.forniandi.circlegathering.utils.createCustomTempFileImg

class CameraXActivity : AppCompatActivity() {

    private lateinit var binding:ActivityCameraXBinding
    private lateinit var objCameraX: Camera
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private var flashMode = FLASH_MODE_OFF


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraXBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgSwitchPositionCamera.setOnClickListener {
            cameraSelector =
                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            initiateCamera()
        }

        binding.imgCaptureImage.setOnClickListener {
            actionTakePhoto()
        }

        binding.imgButtonFlash.setOnClickListener {
            setFlashTorchCamera(objCameraX)
        }

    }

    private fun setFlashTorchCamera(objCameraX: Camera) {
        if (objCameraX.cameraInfo.hasFlashUnit()){
            if (objCameraX.cameraInfo.torchState.value == 0){
                objCameraX.cameraControl.enableTorch(true)
                binding.imgButtonFlash.setImageResource(R.drawable.ic_flash_off_black_48dp)
            }else{
                objCameraX.cameraControl.enableTorch(false)
                binding.imgButtonFlash.setImageResource(R.drawable.ic_flash_on_black_48dp)
            }
        }else{
            Toast.makeText(
                this,
                "Flash is Not Available",
                Toast.LENGTH_LONG
            ).show()
            binding.imgButtonFlash.isEnabled = false
        }
    }


    private fun actionTakePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = createCustomTempFileImg(application)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val intent = Intent()
                    intent.putExtra("pictCameraX", photoFile)
                    intent.putExtra(
                        "isBackCameraX",
                        cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                    )
                    setResult(CAMERAX_RESULT, intent)
                    finish()
                }

                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraXActivity,
                        "Gagal mengambil gambar.",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, "onError: ${exc.message}")
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()
        orientationEventListener.enable()
    }

    override fun onStop() {
        super.onStop()
        orientationEventListener.disable()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
        initiateCamera()
    }

    private fun initiateCamera() {
        val cameraProviderObject = ProcessCameraProvider.getInstance(this)

        cameraProviderObject.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderObject.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                objCameraX=cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Toast.makeText(
                    this@CameraXActivity,
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "startCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }




    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setFlashModeListener(){
        binding.imgButtonFlash.setOnClickListener {
            if (FLASH_MODE_OFF == flashMode){
                flashMode = FLASH_MODE_ON
                binding.imgButtonFlash.setImageDrawable(getDrawable(R.drawable.ic_flash_on_black_48dp))
            }else{
                binding.imgButtonFlash.setImageDrawable(getDrawable(R.drawable.ic_flash_off_black_48dp))
                flashMode = FLASH_MODE_OFF
            }
        }
    }


    private val orientationEventListener by lazy {
        object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) {
                    return
                }

                val rotation = when (orientation) {
                    in 45 until 135 -> Surface.ROTATION_270
                    in 135 until 225 -> Surface.ROTATION_180
                    in 225 until 315 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageCapture?.targetRotation = rotation
            }
        }
    }


    companion object {
        private const val TAG = "CameraActivity"
        const val CAMERAX_RESULT = 200
    }
}
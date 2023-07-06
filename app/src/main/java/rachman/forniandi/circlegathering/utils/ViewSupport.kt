package rachman.forniandi.circlegathering.utils

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.view.View
import android.widget.TextView
import rachman.forniandi.circlegathering.R
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


private const val FILE_DATE_FORMAT = "dd-MMM-yyyy"
val timeStampImg: String = SimpleDateFormat(
    FILE_DATE_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

@SuppressLint("SimpleDateFormat")
fun getStringDate(date: String?): String? {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val outputDate = SimpleDateFormat(FILE_DATE_FORMAT)
    var d: Date? = null
    try {
        d = dateFormat.parse(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return outputDate.format(d)
}


fun createCustomTempFileImg(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStampImg, ".jpg", storageDir)
}

fun createFileImg(application: Application): File {
    val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
        File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
    }

    val outputDirectory = if (
        mediaDir != null && mediaDir.exists()
    ) mediaDir else application.filesDir

    return File(outputDirectory, "$timeStampImg.jpg")
}

fun uriImgToFileImg(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createCustomTempFileImg(context)

    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()

    return myFile
}





fun View.animateLoadingProcessData(isVisible: Boolean, duration: Long = 300) {
    ObjectAnimator
        .ofFloat(this, View.ALPHA, if (isVisible) 1f else 0f)
        .setDuration(duration)
        .start()
}
package rachman.forniandi.circlegathering.utils

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import android.view.View
import rachman.forniandi.circlegathering.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
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

fun String?.getTimeElapseFormat(): String {
    if (this.isNullOrEmpty()) return "Unknown"

    val format = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    val sdf = SimpleDateFormat(format, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("GMT")
    }

    val pastTime = sdf.parse(this)?.time ?: return "Unknown"
    val diff = System.currentTimeMillis() - pastTime

    val oneMin = 60_000L
    val oneHour = 60 * oneMin
    val oneDay = 24 * oneHour
    val oneMonth = 30 * oneDay
    val oneYear = 365 * oneDay

    return when {
        diff >= oneYear -> "${diff / oneYear} years ago"
        diff >= oneMonth -> "${diff / oneMonth} months ago"
        diff >= oneDay -> "${diff / oneDay} days ago"
        diff >= oneHour -> "${diff / oneHour} hours ago"
        diff >= oneMin -> "${diff / oneMin} min ago"
        else -> "Just now"
    }
}

fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
    val matrix = Matrix()
    return if (isBackCamera) {
        matrix.postRotate(90f)
        Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    } else {
        matrix.postRotate(-90f)
        matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }
}

fun bitmapFromURL(context: Context, urlString: String): Bitmap {
    return try {
        /* allow access content from URL internet */
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        /* fetch image data from URL */
        val url = URL(urlString)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val input: InputStream = connection.inputStream
        BitmapFactory.decodeStream(input)
    } catch (e: IOException) {
        BitmapFactory.decodeResource(context.resources, R.drawable.error_placeholder)
    }
}




fun createCustomTempFileImg(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStampImg, ".jpg", storageDir)
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
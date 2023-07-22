package rachman.forniandi.circlegathering.utils.supportWorker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import rachman.forniandi.circlegathering.R
import rachman.forniandi.circlegathering.activities.MainActivity
import rachman.forniandi.circlegathering.utils.ConstantsMain

/*
class CoreWorker (
    context: Context,
    params: WorkerParameters
) : Worker(context, params){
    override fun doWork(): Result {
        return try {
            sendNotification()
            MyStoryStackWidget.notifyDataSetChanged(applicationContext)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun sendNotification() {
        val notification_id = 0

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(ConstantsMain.NOTIFICATION_ID,notification_id)

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)as NotificationManager

        val titleNotification = applicationContext.getString(R.string.notification_title)
        val subtitleNotification = applicationContext.getString(R.string.notification_subtitle)

        val bitmap = applicationContext.vectorToBitmap(R.drawable.ic_vector_logo)

        val bigPicStyle = NotificationCompat.BigPictureStyle()
            .bigPicture(bitmap)
            .bigLargeIcon(null)

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext,ConstantsMain.NOTIFICATION_CHANNEL)
            .setContentTitle(titleNotification)
            .setContentText(subtitleNotification)
            .setSmallIcon(R.drawable.ic_stat_notification)
            .setLargeIcon(bitmap)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setStyle(bigPicStyle)
            .setAutoCancel(true)

        notification.priority = NotificationCompat.PRIORITY_MAX

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notification.setChannelId(ConstantsMain.NOTIFICATION_CHANNEL)

            val ringtoneManager = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            val channel = NotificationChannel(
                ConstantsMain.NOTIFICATION_CHANNEL,
                ConstantsMain.NOTIFICATION_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100,200,300,400,500,400,300,200,400)
            channel.setSound(ringtoneManager, audioAttributes.build())
            notificationManager.createNotificationChannel(channel)

            notificationManager.notify(notification_id, notification.build())
        }
    }


    private fun Context.vectorToBitmap(drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(this, drawableId) ?: return null
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        ) ?: null
        val canvas = bitmap?.let { Canvas(it) }
        if (canvas != null) {
            drawable.setBounds(0, 0, canvas.width, canvas.height)
        }
        if (canvas != null) {
            drawable.draw(canvas)
        }
        return bitmap
    }
}
*/

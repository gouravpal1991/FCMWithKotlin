package com.sc.fcmpushnotification

import android.app.NotificationManager
import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (intent?.hasExtra("ID") == true) {
            val notificationId = intent.getIntExtra("ID", 0)
            notificationManager.cancel(notificationId)
            return
        }

        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        if (remoteInput != null) {
            val feedback = remoteInput.getCharSequence("DirectReplyNotification")
            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.notification_logo)
                .setContentTitle("Thank you for sharing your feedback!!!")
            notificationManager.notify(0, builder.build())
        }
    }
}
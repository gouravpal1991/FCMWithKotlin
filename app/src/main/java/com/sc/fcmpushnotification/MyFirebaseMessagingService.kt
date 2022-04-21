package com.sc.fcmpushnotification

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONArray

const val channelId = "notification_channel"
const val channelName = "com.sc.fcmpushnotification"

class MyFirebaseMessagingService : FirebaseMessagingService() {
    //generate the notification
    //attach the notification created with the custom layout
    //display the notification

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {

            handleNotificationTypes(remoteMessage)
//            topicMessage(remoteMessage)
            return
        }

        if (remoteMessage.notification != null) {
            fcmNotification(remoteMessage.notification)
        }
    }

    //1. This function is to execute for default FCM Notification
    private fun fcmNotification(notification: RemoteMessage.Notification?) {
        val title = notification?.title
        val message = notification?.body
        notifyUser(title, message)
    }

    //2. This function is to execute for default topic message
    private fun topicMessage(remoteMessage: RemoteMessage) {
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]
        notifyUser(title, message)
    }

    //3. This function is to handle different type of notifications
    private fun handleNotificationTypes(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            val notificationData = remoteMessage.data

            when (notificationData["type"]) {
                "BIGTEXT" -> {
                    showBigTextNotification(notificationData)
                }
                "BIGPIC" -> {
                    showBigPictureNotification(notificationData)
                }
                "ACTIONS" -> {
                    showActionsNotifications(notificationData)
                }
                "DIRECTREPLY" -> {
                    showDirectReplyNotifications(notificationData)
                }
                "INBOX" -> {
                    showInboxNotification(notificationData)
                }
                "MESSAGE"->{
                    showMessageStyleNotification(notificationData)
                }
            }

        }
    }


    private fun showBigTextNotification(notificationData: MutableMap<String, String>) {
        val title = notificationData["title"]
        val message = notificationData["message"]
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        /*
        style for showing big text
         */
        val style = NotificationCompat.BigTextStyle()
        style.bigText(message)
        style.setSummaryText(title)

        val defaultSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification_logo)
            .setContentTitle(title)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setColor(Color.BLUE)
            .setSound(defaultSoundURI)
            .setContentIntent(pendingIntent)
            .setStyle(style)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "FCM Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun showBigPictureNotification(notificationData: MutableMap<String, String>) {
        val title = notificationData["title"]
        val message = notificationData["message"]
        val imageUrl = notificationData["imageUrl"]
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        /*
        style for showing big picture
         */
        val style = NotificationCompat.BigPictureStyle()
        style.setBigContentTitle(title)
        style.setSummaryText(message)
        style.bigPicture(Glide.with(this).asBitmap().load(imageUrl).submit().get())

        val defaultSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification_logo)
            .setContentTitle(title)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setColor(Color.BLUE)
            .setSound(defaultSoundURI)
            .setContentIntent(pendingIntent)
            .setStyle(style)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "FCM Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun showActionsNotifications(notificationData: MutableMap<String, String>) {
        val title = notificationData["title"]
        val message = notificationData["message"]

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val cancelIntent = Intent(this, NotificationReceiver::class.java)
        cancelIntent.putExtra("ID", 0)
        val cancelPendingIntent =
            PendingIntent.getBroadcast(baseContext, 0, cancelIntent, PendingIntent.FLAG_IMMUTABLE)

        val defaultSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setColor(Color.BLUE)
            .setSound(defaultSoundURI)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_menu_view, "VIEW", pendingIntent)
            .addAction(android.R.drawable.ic_delete, "DISMISS", cancelPendingIntent)


        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "FCM Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)

        }
        notificationManager.notify(0, notificationBuilder.build())

    }

    private fun showDirectReplyNotifications(notificationData: MutableMap<String, String>) {
        val title = notificationData["title"]
        val message = notificationData["message"]

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val cancelIntent = Intent(this, NotificationReceiver::class.java)
        cancelIntent.putExtra("ID", 0)
        val cancelPendingIntent =
            PendingIntent.getBroadcast(baseContext, 0, cancelIntent, PendingIntent.FLAG_IMMUTABLE)

        val feedbackIntent = Intent(this, NotificationReceiver::class.java)
        val feedbackPendingIntent =
            PendingIntent.getBroadcast(this, 100, feedbackIntent, PendingIntent.FLAG_MUTABLE)

        val remoteInput = RemoteInput.Builder("DirectReplyNotification")
            .setLabel(message)
            .build()

        val action = NotificationCompat.Action.Builder(
            android.R.drawable.ic_delete,
            "Write here...", feedbackPendingIntent
        )
            .addRemoteInput(remoteInput)
            .build()

        val defaultSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setColor(Color.RED)
            .setSound(defaultSoundURI)
            .setContentIntent(pendingIntent)
            .addAction(action)
            .addAction(android.R.drawable.ic_menu_compass,"Cancel", cancelPendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "FCM Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)

        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun showInboxNotification(notificationData: MutableMap<String, String>) {
        val title = notificationData["title"]
        val message = notificationData["message"]
        val contentList = JSONArray(notificationData["contentList"])
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        /*
        style for showing big text
         */
        val style = NotificationCompat.InboxStyle()
        style.setSummaryText(message)
        style.setBigContentTitle(title)
        for (i in 0 until contentList.length()) {
            val emailName: String = contentList.getString(i)
            style.addLine(emailName)
        }

        val defaultSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setColor(Color.BLUE)
            .setSound(defaultSoundURI)
            .setContentIntent(pendingIntent)
            .setStyle(style)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "FCM Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun showMessageStyleNotification(notificationData: MutableMap<String, String>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        /*
        style for showing big text
         */

        val style = NotificationCompat.MessagingStyle("Gourav Pal")
       style.addMessage(NotificationCompat.MessagingStyle.Message("Do we have meeting at 4PM",0,Person.Builder().setName("Gourav Pal").build()))
        style.addMessage(NotificationCompat.MessagingStyle.Message("I have a conflict, lets connect at 5",0,Person.Builder().setName("Jitendra Kumar").build()))

        val defaultSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification_logo)
            .setAutoCancel(true)
            .setColor(Color.BLUE)
            .setSound(defaultSoundURI)
            .setContentIntent(pendingIntent)
            .setStyle(style)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "FCM Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun notifyUser(title: String?, message: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setColor(Color.BLUE)
            .setSound(defaultSoundURI)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "FCM Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        //store or save the updatedId on server here
    }

//    fun generateNotification(title: String, message: String, imageUrl:Uri) {
//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//
//        val pendingActivity =
//            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//
//        //channelId and channelName after update to android oreo
//        var builder: NotificationCompat.Builder =
//            NotificationCompat.Builder(applicationContext, channelId)
//                .setSmallIcon(R.mipmap.ic_launcher_round)
//                .setAutoCancel(true)
//                .setVibrate(longArrayOf(1000, 1000, 1000, 1000))//time in miliseconds
//                .setOnlyAlertOnce(true)
//                .setContentIntent(pendingActivity)
//
//        builder = builder.setContent(getRemoteView(title, message, imageUrl))
//
//        val notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val notificationChannel =
//                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
//            notificationManager.createNotificationChannel(notificationChannel)
//            notificationManager.notify(0, builder.build())
//        }
//    }
//
//    fun getRemoteView(title: String, message: String, imageUrl: Uri): RemoteViews {
//
//        val remoteView = RemoteViews(channelName, R.layout.notification)
//        remoteView.setTextViewText(R.id.title, title)
//        remoteView.setTextViewText(R.id.message, message)
//        remoteView.setImageViewUri(R.id.app_logo,imageUrl)
////        remoteView.setImageViewResource(R.id.app_logo, R.mipmap.ic_launcher_round)
//        return remoteView
//    }
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        if (remoteMessage.notification != null) {
//            generateNotification(
//                remoteMessage?.notification?.title!!,
//                remoteMessage?.notification?.body!!,
//                remoteMessage?.notification?.imageUrl!!,
//            remoteMessage?.notification?.clickAction!!
//            )
//        }
}

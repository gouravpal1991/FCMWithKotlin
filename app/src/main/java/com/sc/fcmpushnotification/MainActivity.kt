package com.sc.fcmpushnotification

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSubscribe = findViewById<View>(R.id.btn_subscribe) as Button
        buttonSubscribe.setOnClickListener {
            FirebaseMessaging.getInstance().subscribeToTopic("PushNotifications")
        }


        FirebaseMessaging.getInstance().token.addOnCompleteListener { it ->
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            }

            //to get FCM Registration Token
            val token = it.result
            Toast.makeText(this,token,Toast.LENGTH_SHORT).show()
            Log.d("token",token)

            val database = Firebase.database
            val myRef = database.getReference("message")

            myRef.setValue(token)

        }

    }
}
package com.luccas.passelivredocumentos.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {

    val TAG = "PushNotifService"
    lateinit var name: String

    override fun onMessageReceived(p0: RemoteMessage) {
        Log.i("FCM", p0.messageId)
        super.onMessageReceived(p0)
    }

}
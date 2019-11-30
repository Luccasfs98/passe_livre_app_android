package com.luccas.passelivredocumentos.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.luccas.passelivredocumentos.R
import com.luccas.passelivredocumentos.ui.MainActivity

class MyFirebaseInstanceIdService : FirebaseMessagingService() {

    val TAG = "PushNotifService"
    lateinit var name: String



}
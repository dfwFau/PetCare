package com.example.petcare.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.petcare.util.NotificationHelper

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Pet Care Reminder"
        val message = intent.getStringExtra("message") ?: "Time to check on your pet!"
        NotificationHelper.showNotification(context, title, message)
    }
}

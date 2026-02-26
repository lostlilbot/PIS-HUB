package com.pishub.app.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Schedule workers after boot
            SyncWorker.schedule(context)
            WeatherNewsWorker.schedule(context)
        }
    }
}

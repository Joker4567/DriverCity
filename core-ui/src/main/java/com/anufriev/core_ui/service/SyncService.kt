package com.anufriev.core_ui.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.anufriev.core_ui.R
import com.anufriev.utils.platform.NotificationChannels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SyncService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getBooleanExtra("stopService", true)?.let { isStopService ->
            if(isStopService){
                stopServiceWithNotification()
            } else {
                startService()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startService(){
        startForeground(
            CHANNEL_ID, createNotification()
        )
    }

    private fun stopServiceWithNotification(){
        val updatedNotification = createNotification("Снят с линии")

        NotificationManagerCompat.from(this)
            .notify(CHANNEL_ID, updatedNotification)
        stopForeground(true)
        stopSelf()
    }

    private fun createNotification(content:String = ""): Notification {
        return NotificationCompat.Builder(this, NotificationChannels.DOWNLOAD_CHANNEL_ID)
            .setContentText(getString(R.string.app_name))
            .setStyle(NotificationCompat.BigTextStyle().bigText(if(content.isEmpty()) "Отправка местоположения раз в 3 минуты" else content))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOnlyAlertOnce(true)
            .build()
    }

    override fun onDestroy() {
        stopServiceWithNotification()
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = 13435
    }
}
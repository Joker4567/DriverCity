package com.anufriev.core_ui.service

import android.Manifest
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.anufriev.core_date.repository.CityDriveRepository
import com.anufriev.core_date.storage.Pref
import com.anufriev.core_ui.R
import com.anufriev.utils.Const
import com.anufriev.utils.ext.getGPS
import com.anufriev.utils.platform.NotificationChannels
import com.anufriev.utils.platform.State
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SyncService : Service() {

    @Inject lateinit var repository: CityDriveRepository

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getBooleanExtra("stopService", true)?.let { isStopService ->
            if(isStopService){
                Const.isActive = false
                stopServiceWithNotification()
            } else {
                Const.isActive = true
                startService()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startService(){
        startForeground(
            CHANNEL_ID, createNotification()
        )
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                val isLocationPermissionGranted = ActivityCompat.checkSelfPermission(
                    applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                if (isLocationPermissionGranted && getGPS(applicationContext)) {
                    LocationServices.getFusedLocationProviderClient(applicationContext)
                        .getCurrentLocation(LocationRequest.PRIORITY_LOW_POWER, null)
                        .addOnSuccessListener { location ->
                            if (location != null) {
                                val phone = Pref(applicationContext).phone
                                CoroutineScope(Dispatchers.IO).launch {
                                    phone?.let { phone ->
                                        repository.postLocation(
                                            phone,
                                            location.latitude,
                                            location.longitude,
                                            Pref(applicationContext).radius,
                                            true,
                                            {
                                                //Запись успешно добавлена
                                                Timber.d("Запись с местоположением успешно обновлена")
                                            },
                                            {
                                                if (it is State.Error) {
                                                    Timber.e("Ошибка при отправке местоположения на сервер")
                                                }
                                            }
                                        )
                                    }
                                }
                            } else {
                                Timber.e("Не удалось получить местоположение")
                            }
                        }
                        .addOnCanceledListener { Timber.d("Запрос локации был отменен") }
                        .addOnFailureListener { Timber.e("Запрос локации завершился неудачно") }

                }
                delay(3 * 1000 * 60)
            }
        }
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
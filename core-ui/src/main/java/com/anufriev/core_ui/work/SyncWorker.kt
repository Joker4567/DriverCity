package com.anufriev.core_ui.work

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.anufriev.core_date.repository.CityDriveRepository
import com.anufriev.core_date.storage.Pref
import com.anufriev.utils.ext.getGPS
import com.anufriev.utils.platform.State
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: CityDriveRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val radius = inputData.getString(KEY_RADIUS)
        Timber.d("work started")
        val isLocationPermissionGranted = ActivityCompat.checkSelfPermission(
            applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (isLocationPermissionGranted && getGPS(this.applicationContext)) {
            CoroutineScope(Dispatchers.IO).launch {
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
                                        radius.toString().toInt(),
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
            return Result.success()
        } else {
            return Result.failure()
        }
    }

    companion object {
        const val KEY_RADIUS = "download url"
    }
}
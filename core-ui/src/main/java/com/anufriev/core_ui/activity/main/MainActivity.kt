package com.anufriev.core_ui.activity.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isVisible
import androidx.work.*
import com.anufriev.core_date.storage.Pref
import com.anufriev.core_ui.service.SyncService
import com.anufriev.core_ui.work.SyncWorker
import com.anufriev.utils.ext.getGPS
import com.anufriev.utils.ext.observeLifeCycle
import com.anufriev.utils.ext.toast
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity(com.anufriev.drawable.R.layout.main_activity) {

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribe()
        bind()
        initWorker()
    }

    private fun subscribe(){
        observeLifeCycle(mainViewModel.stateWork, {
            if(it!!) toast("Выход на линию осуществлён") else toast("Выход с линии успешно произведён")
        })
    }

    private fun bind(){
        var textPhone = this.findViewById<TextView>(com.anufriev.drawable.R.id.edit_phone)
        val radius = this.findViewById<TextView>(com.anufriev.drawable.R.id.etRadius)
        Pref(context = applicationContext).phone?.let {
            textPhone.setText(it)
        }
        this.findViewById<Button>(com.anufriev.drawable.R.id.btStart).setOnClickListener {
            if(textPhone.text.isNotEmpty() && radius.text.isNotEmpty()) {
                Pref(context = applicationContext).phone = textPhone.text.toString()
                //Запуск сервиса с отправкой местоположения
                getGeo()
            } else if(textPhone.text.isEmpty()) {
                toast("Введите номер телефона для выхода на линию")
            } else if(radius.text.isEmpty()){
                toast("Введите радиус принятия заказа для выхода на линию")
            }
        }
        this.findViewById<Button>(com.anufriev.drawable.R.id.btStop).setOnClickListener {
            if(textPhone.text.isNotEmpty()){
                Pref(context = applicationContext).phone = textPhone.text.toString()
                //Остановка сервиса + удалить телефон из БД для звонков
                removePhoneWithStopService()
                changeButtonStart(false)
            } else {
                toast("Введите номер телефона для выхода с линии")
            }
        }
    }

    private fun initWorker(){
        WorkManager.getInstance(this)
            .getWorkInfosForUniqueWorkLiveData(DOWNLOAD_WORK_ID)
            .observe(this, { if (it.isNotEmpty()) handleWorkInfo(it.first()) })
    }

    private fun handleWorkInfo(workInfo: WorkInfo) {
        val isFinished = workInfo.state.isFinished
        if (isFinished) {
            toast("Геопозиция отправлена на сервер")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CODE_MAP) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                getGeo()
            } else {
                val needRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                if (needRationale) {
                    toast("Для получения текущего местоположения, требуется разрешение!")
                }
            }
        }
    }

    private fun getGeo() {
        val isLocationPermissionGranted = ActivityCompat.checkSelfPermission(
            applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        if (isLocationPermissionGranted && getGPS(this.applicationContext)) {
            CoroutineScope(Dispatchers.IO).launch {
                LocationServices.getFusedLocationProviderClient(applicationContext)
                    .getCurrentLocation(LocationRequest.PRIORITY_LOW_POWER, null)
                    .addOnSuccessListener {
                        if(it != null) {
                            startWorkSync()
                        } else {
                            toast("Не удалось получить местоположение")
                        }
                    }
                    .addOnCanceledListener { toast("Запрос локации был отменен") }
                    .addOnFailureListener { toast("Запрос локации завершился неудачно") }
            }
        } else if (!getGPS(this.applicationContext)) {
            toast("Включите GPS в настройках телефона")
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                CODE_MAP
            )
        }
    }

    private fun startWorkSync() {
        changeButtonStart()
        val radius = this.findViewById<TextView>(com.anufriev.drawable.R.id.etRadius).text.toString()

        val workData = workDataOf(
            SyncWorker.KEY_RADIUS to radius
        )

        val workConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_ROAMING)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<SyncWorker>(2, TimeUnit.MINUTES)
            .setInputData(workData)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
            .setConstraints(workConstraints)
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)

        val downloadIntent = Intent(this, SyncService::class.java)
        downloadIntent.putExtra("stopService", false)
        startService(downloadIntent)
    }

    private fun changeButtonStart(start:Boolean = true){
        this.findViewById<Button>(com.anufriev.drawable.R.id.btStart).isVisible = !start
        this.findViewById<Button>(com.anufriev.drawable.R.id.btStop).isVisible = start
    }

    private fun removePhoneWithStopService(){
        val downloadIntent = Intent(this, SyncService::class.java)
        downloadIntent.putExtra("stopService", true)
        startService(downloadIntent)
        WorkManager.getInstance(applicationContext).cancelUniqueWork(DOWNLOAD_WORK_ID)
    }

    override fun onDestroy() {
        removePhoneWithStopService()
        super.onDestroy()
    }

    companion object {
        private const val CODE_MAP = 1023;
        private const val DOWNLOAD_WORK_ID = "download_work"
    }
}
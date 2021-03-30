package com.anufriev.core_ui.activity.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.anufriev.core_date.storage.Pref
import com.anufriev.core_ui.activity.settings.SettingsActivity
import com.anufriev.core_ui.service.SyncService
import com.anufriev.utils.Const
import com.anufriev.utils.ext.getGPS
import com.anufriev.utils.ext.observeLifeCycle
import com.anufriev.utils.ext.toast
import com.anufriev.utils.platform.DriverState
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(com.anufriev.drawable.R.layout.main_activity) {

    private val mainViewModel by viewModels<MainViewModel>()
    private lateinit var textState: TextView
    private lateinit var btStart: Button
    private lateinit var btStop: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind()
        subscribe()
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.getDriver(applicationContext)
    }

    private fun bind() {
        textState = this.findViewById(com.anufriev.drawable.R.id.tvState)
        textState.text = "Статус: в ожидании"
        btStart = this.findViewById<Button>(com.anufriev.drawable.R.id.btStart)
        btStart.setOnClickListener {
            if (Pref(applicationContext).phone.isNullOrEmpty().not() &&
                Pref(applicationContext).radius != 0
            ) {
                getGeo()
            } else if (Pref(applicationContext).phone.isNullOrEmpty()) {
                toast("Введите номер телефона в настройках")
            } else if (Pref(applicationContext).radius == 0) {
                toast("Введите радиус принятия заказа в настройках")
            }
        }
        btStop = this.findViewById<Button>(com.anufriev.drawable.R.id.btStop)
        btStop.setOnClickListener {
            if(Const.isActive) {
                //Остановка сервиса
                removePhoneWithStopService()
            }
            mainViewModel.stopDriver(applicationContext)
            changeButtonStart(false)
        }
        this.findViewById<ImageView>(com.anufriev.drawable.R.id.ivSettings).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        changeButtonStart(false)
        textState.setOnClickListener {
            textState.text = "Статус: в ожидании"
            mainViewModel.getDriver(applicationContext)
        }
    }

    private fun subscribe() {
        observeLifeCycle(mainViewModel.driver, {
            it?.let {
                textState.text =
                    if (it.state)
                        "Статус: в ожидании заказа"
                    else
                        "Статус: снят с линии"
                Const.isActive = it.state
                changeButtonStart(it.state)
            }
        })
        observeLifeCycle(mainViewModel.error, {
            it?.let {
                textState.text = "Статус: $it, нажмите на статус для повторной проверки"
            }
        })
        observeLifeCycle(mainViewModel.stopDriver, {
            it?.let {
                textState.text = "Статус: $it"
            }
        })
        lifecycleScope.launch {
            DriverState.stateDriver
                .debounce(500)
                .distinctUntilChanged()
                .collect {
                    when(it){
                        2 -> {
                            textState.text = "Статус: в ожидании заказа"
                        }
                        3 -> {
                            textState.text = "Статус: снят с линии"
                        }
                        4 -> {
                            textState.text = "Статус: не удалось получить местоположение"
                        }
                    }
                }
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
                        if (it != null) {
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
        val downloadIntent = Intent(this, SyncService::class.java)
        downloadIntent.putExtra("stopService", false)
        startService(downloadIntent)
    }

    private fun changeButtonStart(start: Boolean = true) {
        btStart.isVisible = !start
        btStop.isVisible = start
    }

    private fun removePhoneWithStopService() {
        val downloadIntent = Intent(this, SyncService::class.java)
        downloadIntent.putExtra("stopService", true)
        startService(downloadIntent)
    }

    companion object {
        private const val CODE_MAP = 1023;
    }
}
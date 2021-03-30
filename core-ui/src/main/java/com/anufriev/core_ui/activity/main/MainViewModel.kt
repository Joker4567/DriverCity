package com.anufriev.core_ui.activity.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.anufriev.core_date.model.Driver
import com.anufriev.core_date.repository.CityDriveRepository
import com.anufriev.core_date.storage.Pref
import com.anufriev.utils.platform.BaseViewModel
import com.anufriev.utils.platform.State
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: CityDriveRepository
) : BaseViewModel() {
    var driver = MutableLiveData<Driver>()
    var error = MutableLiveData<String>()
    var stopDriver = MutableLiveData<String>()

    fun getDriver(context: Context){
        Pref(context).phone?.let {
            launchIO {
                repository.getStateDriver(it, { driverLocal ->
                    driverLocal?.let {
                        launch {
                            driver.postValue(driverLocal)
                        }
                    }
                }, ::error)
            }
        }
    }

    fun stopDriver(context: Context){
        val phone = Pref(context).phone
        val radius = Pref(context).radius
        if(phone.isNullOrEmpty().not() && radius != 0){
            launchIO {
                repository.changeStateDriver(phone!!, false, {
                    stopDriver.postValue("снят с линии")
                }, ::error)
            }
        }
    }

    private fun error(state: State){
        if(state != State.Loading && state != State.Loaded){
            //Ошибка
            error.postValue("Ошибка получения данных")
        }
    }
}
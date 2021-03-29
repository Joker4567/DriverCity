package com.anufriev.core_ui.activity.settings

import androidx.lifecycle.MutableLiveData
import com.anufriev.core_date.repository.CityDriveRepository
import com.anufriev.utils.platform.BaseViewModel
import com.anufriev.utils.platform.State
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: CityDriveRepository
) : BaseViewModel(){

    var saveState = MutableLiveData<String>()

    fun postDriver(phone:String, radius:Int){
        launchIO {
            repository.postLocation(
                phone,
                0.0,
                0.0,
                radius,
                false,
                {
                    saveState.postValue("Настройки успешно сохранены")
                },
                ::error
            )
        }
    }

    private fun error(state:State){
        if(state != State.Loading && state != State.Loaded){
            //Ошибка
            saveState.postValue("Ошибка сохранения")
        }
    }
}
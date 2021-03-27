package com.anufriev.core_ui.activity.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
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

    var stateWork = MutableLiveData<Boolean>()

    fun removePhone(context: Context) {
        val phone = Pref(context).phone
        launchIO {
            phone?.let {
                repository.removePhone(it, {
                    //Телефон успешно удален из базы
                    stateWork.postValue(false)
                }, {})
            }
        }
    }
}
package com.anufriev.core_ui.activity.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.anufriev.core_date.storage.Pref
import com.anufriev.core_ui.R
import com.anufriev.core_ui.activity.main.MainActivity
import com.anufriev.utils.Const
import com.anufriev.utils.ext.gone
import com.anufriev.utils.ext.observeLifeCycle
import com.anufriev.utils.ext.show
import com.anufriev.utils.ext.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity(R.layout.settings_activity) {
    private val settingViewModel by viewModels<SettingsViewModel>()
    private lateinit var progress: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind()
        subscribe()
    }

    private fun subscribe(){
        observeLifeCycle(settingViewModel.saveState, {
            it?.let {
                progress.text = it
                progress.show()
                progress.postDelayed({
                    progress.gone()
                }, 2000)
            }
        })
    }

    private fun bind(){
        var textPhone = this.findViewById<TextView>(com.anufriev.drawable.R.id.edit_phone)
        val radius = this.findViewById<TextView>(com.anufriev.drawable.R.id.etRadius)
        progress  = this.findViewById(com.anufriev.drawable.R.id.tvSaveProgress)
        val btClear = this.findViewById<Button>(com.anufriev.drawable.R.id.btClearSettings)
        progress.gone()
        Pref(context = applicationContext).phone?.let {
            textPhone.text = it
        }
        Pref(context = applicationContext).radius.let {
            radius.text = if(it != 0) it.toString() else ""
        }
        if(radius.text.isNotEmpty() && textPhone.text.isNotEmpty())
            btClear.show()
        else
            btClear.gone()
        this.findViewById<Button>(com.anufriev.drawable.R.id.btSaveSettings).setOnClickListener {
            if(textPhone.text.isNotEmpty() && radius.text.isNotEmpty() && Const.isActive.not()) {
                Pref(context = applicationContext).phone = textPhone.text.toString()
                Pref(context = applicationContext).radius = radius.text.toString().toInt()
                settingViewModel.postDriver(textPhone.text.toString(), radius.text.toString().toInt())
                btClear.show()
            } else if(textPhone.text.isEmpty()) {
                toast("Введите номер телефона")
            } else if(radius.text.isEmpty()){
                toast("Введите радиус принятия заказа")
            } else if(Const.isActive) {
                toast("Для сохранения настроек выйдите с линии")
            }
        }
        btClear.setOnClickListener {
            if(Const.isActive) {
                toast("Для сохранения настроек выйдите с линии")
                return@setOnClickListener
            }
            Pref(context = applicationContext).clearPhone()
            Pref(context = applicationContext).clearRadius()
            settingViewModel.postDriver(textPhone.text.toString(), radius.text.toString().toInt())
            radius.text = ""
            textPhone.text = ""
            btClear.gone()
        }
        this.findViewById<ImageView>(com.anufriev.drawable.R.id.ivHome).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
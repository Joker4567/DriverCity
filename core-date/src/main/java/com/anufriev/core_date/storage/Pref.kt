package com.anufriev.core_date.storage

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class Pref(context: Context) {

    private var masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private var sharedPreferences = EncryptedSharedPreferences.create(
        FILE_NAME,
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    var phone: String?
        get() = sharedPreferences.getString(KEY_PHONE, null)
        set(value) {
            sharedPreferences.edit {
                putString(KEY_PHONE, value)
            }
        }

    fun clearPhone() {
        sharedPreferences.edit {
            remove(KEY_PHONE)
        }
    }

    var radius: Int
        get() = sharedPreferences.getInt(KEY_RADIUS, 0)
        set(value) {
            sharedPreferences.edit {
                putInt(KEY_RADIUS, value)
            }
        }

    fun clearRadius() {
        sharedPreferences.edit {
            remove(KEY_RADIUS)
        }
    }

    companion object {
        const val FILE_NAME = "CityPreference"
        const val KEY_RADIUS = "radius"
        const val KEY_PHONE = "phone"
    }
}
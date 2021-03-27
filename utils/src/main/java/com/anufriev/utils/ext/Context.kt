package com.anufriev.utils.ext

import android.content.Context
import android.location.LocationManager
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat

fun Context.getCompatColor(@ColorRes color: Int) = ContextCompat.getColor(this, color)

fun Context.getCompatDrawable(@DrawableRes drawable: Int) =
    AppCompatResources.getDrawable(this, drawable)

fun getGPS(context: Context): Boolean {
    val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}
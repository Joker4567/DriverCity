@file:Suppress("DEPRECATION")

package com.anufriev.utils.ext

import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment

fun Activity.setStatusBarColor(@ColorInt color: Int) {
    window.statusBarColor = color
}

fun Activity.setStatusBarLightMode(
    isLightMode: Boolean
) {
    val decorView = this.window.decorView
    var vis = decorView.systemUiVisibility
    vis = if (isLightMode) {
        vis or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    } else {
        vis and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    }
    decorView.systemUiVisibility = vis
}

fun Fragment.toast(@StringRes stringRes: Int) {
    Toast.makeText(requireContext(), stringRes, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(str:String) {
    Toast.makeText(requireContext(), str, Toast.LENGTH_SHORT).show()
}

fun Activity.toast(@StringRes stringRes: Int) {
    Toast.makeText(this, stringRes, Toast.LENGTH_SHORT).show()
}

fun Activity.toast(str:String) {
    Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
}

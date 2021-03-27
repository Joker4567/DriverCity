package com.anufriev.utils.ext

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun View.show() = run { visibility = View.VISIBLE }

fun View.invisible() = run { visibility = View.INVISIBLE }

fun View.gone() = run { visibility = View.GONE }

fun View.enable() = run { isEnabled = true }

fun View.disable() = run { isEnabled = false }

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}
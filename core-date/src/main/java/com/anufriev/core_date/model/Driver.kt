package com.anufriev.core_date.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Driver(
    @SerializedName("id")
    @Expose
    val id:Int,
    @SerializedName("pointLatitude")
    @Expose
    val pointLatitude:Double,
    @SerializedName("pointLongitude")
    @Expose
    val pointLongitude:Double,
    @SerializedName("radius")
    @Expose
    val radius:Int,
    @SerializedName("state")
    @Expose
    val state:Boolean,
    @SerializedName("phone")
    @Expose
    val phone:String
)

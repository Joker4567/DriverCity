package com.anufriev.core_date.repository

import android.util.Log
import com.anufriev.core_date.api.DriveApi
import com.anufriev.core_date.model.Driver
import com.anufriev.utils.platform.BaseRepository
import com.anufriev.utils.platform.ErrorHandler
import com.anufriev.utils.platform.State
import com.google.gson.internal.LinkedHashTreeMap
import com.google.gson.internal.LinkedTreeMap
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import javax.inject.Inject

interface CityDriveRepository {
    suspend fun postLocation(
        phone:String,
        lat: Double,
        lon: Double,
        radius:Int,
        state:Boolean,
        onSuccess: (Boolean) -> Unit,
        onState: (State) -> Unit
    )

    suspend fun changeStateDriver(
        phone:String,
        state:Boolean,
        onSuccess: (Boolean) -> Unit,
        onState: (State) -> Unit
    )

    suspend fun getStateDriver(
        phone:String,
        onSuccess: (Driver?) -> Unit,
        onState: (State) -> Unit
    )
}

class CityDriveRepositoryImp @Inject constructor(
    errorHandler: ErrorHandler,
    private val api: DriveApi
) : BaseRepository(errorHandler = errorHandler), CityDriveRepository {

    override suspend fun postLocation(
        phone:String,
        lat: Double,
        lon: Double,
        radius:Int,
        state:Boolean,
        onSuccess: (Boolean) -> Unit,
        onState: (State) -> Unit
    ) {
        execute(onSuccess = onSuccess, onState = onState) {
            val jsonObjectString = "{ " +
                    "\"PointLatitude\": ${lat}," +
                    " \"PointLongitude\": $lon, " +
                    " \"Radius\": $radius, " +
                    " \"State\": $state, " +
                    "\"Phone\": ${phone.toLowerCase()} " +
                    "}"
            val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
            api.postLocationWithPhone(requestBody).execute().isSuccessful
        }
    }

    override suspend fun changeStateDriver(
        phone: String,
        state:Boolean,
        onSuccess: (Boolean) -> Unit,
        onState: (State) -> Unit
    ) {
        execute(onSuccess = onSuccess, onState = onState) {
            api.removePhone(phone, state).execute().isSuccessful
        }
    }

    override suspend fun getStateDriver(
        phone: String,
        onSuccess: (Driver?) -> Unit,
        onState: (State) -> Unit
    ) {
        execute(onSuccess = onSuccess, onState = onState) {
            val result = api.getState(phone).execute()
            try {
                val body = (result.body() as LinkedTreeMap<Any, Any>).toMap()
                Driver(
                    (body["id"] as Double).toInt(),
                    (body["pointLatitude"] as Double).toDouble(),
                    (body["pointLongitude"] as Double).toDouble(),
                    (body["radius"] as Double).toInt(),
                    (body["state"] as Boolean),
                    (body["phone"] as String)
                )
            }
            catch (ex:Exception) {
                null
            }
        }
    }

    companion object {
        const val TAG = "CityTransport"
    }
}
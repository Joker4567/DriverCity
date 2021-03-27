package com.anufriev.core_date.repository

import com.anufriev.core_date.api.DriveApi
import com.anufriev.utils.platform.BaseRepository
import com.anufriev.utils.platform.ErrorHandler
import com.anufriev.utils.platform.State
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

interface CityDriveRepository {
    suspend fun postLocation(
        phone:String,
        lat: Double,
        lon: Double,
        radius:Int,
        onSuccess: (Boolean) -> Unit,
        onState: (State) -> Unit
    )

    suspend fun removePhone(
        phone:String,
        onSuccess: (Boolean) -> Unit,
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
        onSuccess: (Boolean) -> Unit,
        onState: (State) -> Unit
    ) {
        execute(onSuccess = onSuccess, onState = onState) {
            val jsonObjectString = "{ " +
                    "\"PointLatitude\": ${lat}," +
                    " \"PointLongitude\": $lon, " +
                    " \"Radius\": $radius, " +
                    "\"Phone\": ${phone.toLowerCase()} " +
                    "}"
            val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
            api.postLocationWithPhone(requestBody).execute().isSuccessful
        }
    }

    override suspend fun removePhone(
        phone: String,
        onSuccess: (Boolean) -> Unit,
        onState: (State) -> Unit
    ) {
        execute(onSuccess = onSuccess, onState = onState) {
            api.removePhone(phone).execute().isSuccessful
        }
    }

    companion object {
        const val TAG = "CityTransport"
    }
}
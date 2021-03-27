package com.anufriev.core_date.api

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DriveApi {
    /*
    200 OK, (запись добавлена)
    400 Bad Response, (не валидные данные)
    500 Internal server error (краш)
    Body:
    {
        "PointLatitude" : 56.3434,
        "PointLongitude" : 48.43545,
        "Phone" : "7777778"
    }
    */
    @POST("api/organizations/phoneDriver")
    fun postLocationWithPhone(@Body body: RequestBody): Call<Unit>
    /*
        200 OK, (запись удалена)
        204 No content, [нет записи]
        500 Internal server error (краш)
    */
    @GET("api/organizations/phoneDriver/{phone}")
    fun removePhone(@Path("phone") phone:String): Call<Unit>
}
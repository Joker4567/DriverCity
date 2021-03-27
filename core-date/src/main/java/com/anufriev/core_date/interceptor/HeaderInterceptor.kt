package com.anufriev.core_date.interceptor

import com.anufriev.core_date.storage.Pref
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class HeaderInterceptor
@Inject
constructor(
    private val pref: Pref
): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
//        when {
//            !pref.authToken.isNullOrEmpty() -> {
//                val newRequest =
//                    chain.request().newBuilder()
//                        //.header("Authorization", "bearer " + pref.authToken.toString())
//                        .header("Content-Type","application/json; charset=UTF-8")
//                        .build()
//
//                return chain.proceed(newRequest)
//            }
//        }
        return chain.proceed(chain.request())
    }
}
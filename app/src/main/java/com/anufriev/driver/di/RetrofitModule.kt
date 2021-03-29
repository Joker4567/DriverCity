package com.anufriev.driver.di

import android.app.Application
import android.util.Log
import com.anufriev.core_date.interceptor.HeaderInterceptor
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.anufriev.core_date.BuildConfig
import com.anufriev.core_date.api.DriveApi
import okhttp3.Cache
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    private const val TIME_OUT_CONNECT = 20L
    private const val TIME_OUT_WRITE = 15L
    private const val TIME_OUT_READ = 15L

    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
    }

    @Singleton
    @Provides
    fun provideCache(application: Application): Cache {
        val cacheSize = 10 * 1024 * 1024
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    @Singleton
    @Provides
    fun buildOkHttp(cache: Cache, headerInterceptor: HeaderInterceptor): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
        with(okHttpClientBuilder) {
            addInterceptor(headerInterceptor)
            cache(cache)
            connectTimeout(TIME_OUT_CONNECT, TimeUnit.SECONDS)
            writeTimeout(TIME_OUT_WRITE, TimeUnit.SECONDS)
            readTimeout(TIME_OUT_READ, TimeUnit.SECONDS)
            followSslRedirects(false)
            followRedirects(false)
            retryOnConnectionFailure(true)
            ignoreAllSSLErrors()
        }
        if (BuildConfig.DEBUG) {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            okHttpClientBuilder.addInterceptor(loggingInterceptor)
            okHttpClientBuilder.addNetworkInterceptor(StethoInterceptor())
        }
        return okHttpClientBuilder.build()
    }

    private fun OkHttpClient.Builder.ignoreAllSSLErrors(): OkHttpClient.Builder {
        val naiveTrustManager = object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) = Unit
            override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) = Unit
        }

        try {
            val insecureSocketFactory = SSLContext.getInstance("SSL").apply {
                val trustAllCerts = arrayOf<TrustManager>(naiveTrustManager)
                init(null, trustAllCerts, SecureRandom())
            }.socketFactory
            sslSocketFactory(insecureSocketFactory, naiveTrustManager)
        }
        catch (ex: NoSuchAlgorithmException)
        {
            Log.e("OkHttpClient","NoSuchAlgorithmException ${ex.message}")
        }
        catch (ex: KeyManagementException)
        {
            Log.e("OkHttpClient","KeyManagementException ${ex.message}")
        }
        catch (ex:Exception)
        {
            Log.e("OkHttpClient","Exception ${ex.message}")
        }
        hostnameVerifier(HostnameVerifier { _, _ -> true })
        return this
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient, gson: Gson): Retrofit.Builder {
        return Retrofit.Builder()
            .client(client)
            .baseUrl(BuildConfig.API_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Singleton
    @Provides
    fun provideRetrofitServiceCatalog(retrofit: Retrofit.Builder): DriveApi {
        return retrofit
            .build()
            .create(DriveApi::class.java)
    }
}
package com.anufriev.driver.di

import android.content.Context
import com.anufriev.utils.platform.ErrorHandler
import com.anufriev.utils.platform.NetworkHandler
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideNetworkHandler(@ApplicationContext context: Context): NetworkHandler {
        return NetworkHandler(context)
    }

    @Singleton
    @Provides
    fun provideErrorHandler(networkHandler: NetworkHandler): ErrorHandler {
        return ErrorHandler(networkHandler, Gson())
    }
}
package com.anufriev.driver.di

import android.content.Context
import com.anufriev.core_date.storage.Pref
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object StorageModule {

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): Pref {
        return Pref(context)
    }
}
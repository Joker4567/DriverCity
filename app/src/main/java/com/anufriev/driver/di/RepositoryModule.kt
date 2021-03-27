package com.anufriev.driver.di

import com.anufriev.core_date.api.DriveApi
import com.anufriev.core_date.repository.CityDriveRepository
import com.anufriev.core_date.repository.CityDriveRepositoryImp
import com.anufriev.utils.platform.ErrorHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

//@Module
//@InstallIn(ViewModelComponent::class)
//object RepositoryModule {
//
//    @Provides
//    fun provideDriveCityRepository(
//        errorHandler: ErrorHandler,
//        api:DriveApi
//    ): CityDriveRepository {
//        return CityDriveRepositoryImp(
//            errorHandler = errorHandler,
//            api = api
//        )
//    }
//}
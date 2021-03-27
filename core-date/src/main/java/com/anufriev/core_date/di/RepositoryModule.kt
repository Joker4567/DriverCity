package com.anufriev.core_date.di

import com.anufriev.core_date.repository.CityDriveRepository
import com.anufriev.core_date.repository.CityDriveRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun providesUserRepository(impl: CityDriveRepositoryImp): CityDriveRepository
}
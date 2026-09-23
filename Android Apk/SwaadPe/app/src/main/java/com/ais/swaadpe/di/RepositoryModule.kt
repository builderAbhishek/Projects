package com.ais.swaadpe.di

import com.ais.swaadpe.data.repository.HomeRepositoryImpl
import com.ais.swaadpe.data.repository.MenuRepositoryImpl
import com.ais.swaadpe.domain.repository.HomeRepository
import com.ais.swaadpe.domain.repository.MenuRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        homeRepositoryImpl: HomeRepositoryImpl
    ): HomeRepository

    @Binds
    @Singleton
    abstract fun bindMenuRepository(
        menuRepositoryImpl: MenuRepositoryImpl
    ): MenuRepository
}

package com.ais.swaadpe.di

import android.content.Context
import com.ais.swaadpe.utils.LocationHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Provides
    @Singleton
    fun provideLocationHelper(@ApplicationContext context: Context): LocationHelper {
        return LocationHelper(context)
    }
}

package com.cupsofcode.photosearch.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AppModule constructor(private val appContext: Context) {
    @Provides
    @Singleton
    fun providesContext() = appContext

    @Singleton
    @Provides
    fun providesSharedPreferences(): SharedPreferences =
        appContext.getSharedPreferences("fancy_app", Context.MODE_PRIVATE)
}
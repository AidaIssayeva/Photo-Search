package com.cupsofcode.photosearch

import android.app.Application
import com.cupsofcode.photosearch.di.AppComponent
import com.cupsofcode.photosearch.di.AppModule


class PhotoSearchApplication : Application() {

    lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()
        component = AppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}
package com.cupsofcode.photosearch.di

import com.cupsofcode.photosearch.network.di.NetworkModule
import com.cupsofcode.photosearch.repository.di.PhotosComponent
import com.cupsofcode.photosearch.repository.di.PhotosDataModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [NetworkModule::class, AppModule::class, PhotosDataModule::class]
)
interface AppComponent : PhotosComponent {

    @Component.Builder
    interface Builder {
        fun appModule(applicationModule: AppModule): Builder
        fun build(): AppComponent
    }

    companion object {
        fun builder(): Builder {
            return DaggerAppComponent.builder()
        }
    }
}
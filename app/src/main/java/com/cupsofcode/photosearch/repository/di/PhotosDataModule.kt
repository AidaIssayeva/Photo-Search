package com.cupsofcode.photosearch.repository.di

import com.cupsofcode.photosearch.repository.PhotoRepository
import com.cupsofcode.photosearch.repository.PhotoRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton


@Module
abstract class PhotosDataModule {
    @Binds
    @Singleton
    abstract fun bindsPhotosRepository(impl: PhotoRepositoryImpl): PhotoRepository
}
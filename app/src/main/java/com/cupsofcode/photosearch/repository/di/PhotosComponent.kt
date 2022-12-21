package com.cupsofcode.photosearch.repository.di

import com.cupsofcode.photosearch.repository.PhotoRepository


interface PhotosComponent {

    fun photosRepository(): PhotoRepository
}
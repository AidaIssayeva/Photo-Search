package com.cupsofcode.photosearch.repository

import com.cupsofcode.photosearch.repository.model.Photo
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single


interface PhotoRepository {
    fun getSavedSearches(): Single<List<String>>
    fun removeSearchItem(query: String): Completable

    fun searchPhotos(searchTerm: String): Single<List<Photo>>
    fun bookmarkToggled(photoId: String): Completable
    fun getPhotoDetails(photoId: String): Observable<Photo>
}
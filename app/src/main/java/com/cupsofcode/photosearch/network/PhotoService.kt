package com.cupsofcode.photosearch.network

import com.cupsofcode.photosearch.di.APIKey
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query


interface PhotoService {

    @GET("?method=flickr.photos.search&format=json&nojsoncallback=1")
    fun searchPhotos(
        @Query("api_key") @APIKey apiKey: String,
        @Query("text") searchTerm: String,
        @Query("per_page") count: Int,
        @Query("page") page: Int
    ): Single<PhotosResponse>
}
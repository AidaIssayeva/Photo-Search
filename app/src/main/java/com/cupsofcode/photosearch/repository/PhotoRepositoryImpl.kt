package com.cupsofcode.photosearch.repository

import android.content.SharedPreferences
import com.cupsofcode.photosearch.di.APIKey
import com.cupsofcode.photosearch.network.PhotoService
import com.cupsofcode.photosearch.repository.model.Photo
import com.cupsofcode.photosearch.repository.model.toPhoto
import com.cupsofcode.photosearch.utils.InMemoryCache
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject


class PhotoRepositoryImpl @Inject constructor(
    private val apiService: PhotoService,
    private val sharedPreferences: SharedPreferences,
    @APIKey private val apiKey: String
) : PhotoRepository {
    private val recentSearches: Queue<String> = LinkedList<String>()

    private val photosInMemoryCache by lazy {
        InMemoryCache<Photo>()
    }

    override fun getSavedSearches(): Single<List<String>> {
        return Single.create<List<String>> { emitter ->
            retrieveSavedSearches()
            emitter.onSuccess(recentSearches.toList())
        }
    }

    override fun removeSearchItem(query: String): Completable {
        return Completable.fromAction {
            recentSearches.remove(query)
            sharedPreferences.edit().putString(SAVED_SEARCHES, recentSearches.joinToString(","))
                .apply()
        }
    }

    override fun searchPhotos(searchTerm: String): Single<List<Photo>> {
        return Completable.fromAction {
            saveSearchQuery(searchTerm)
        }.andThen(
            apiService.searchPhotos(
                apiKey = apiKey,
                searchTerm = searchTerm,
                count = 25,
                page = 1
            ).subscribeOn(Schedulers.io())
                .map { it.photos.photo.map { it.toPhoto() } }
                .flatMapCompletable {
                    photosInMemoryCache.clearAll().andThen(photosInMemoryCache.putAll(it) { it.id })
                }.andThen(photosInMemoryCache.allSingle())
        )

    }

    override fun bookmarkToggled(photoId: String): Completable {
        // TODO: work on bookmark feature
        return Completable.complete()
    }

    private fun retrieveSavedSearches() {
        val saved = sharedPreferences.getString(SAVED_SEARCHES, "")
        val list = saved?.run {
            if (this.isNotEmpty() || this.isNotBlank()) {
                this.split(",")
            } else {
                emptyList()
            }
        } ?: emptyList()
        recentSearches.clear()
        recentSearches.addAll(list)
    }

    private fun saveSearchQuery(newQuery: String) {
        if (recentSearches.size > 8) {
            recentSearches.poll()
        }
        if (!recentSearches.contains(newQuery)) {
            recentSearches.add(newQuery)
        }
        sharedPreferences.edit().putString(SAVED_SEARCHES, recentSearches.joinToString(","))
            .apply()
    }

    override fun getPhotoDetails(photoId: String): Observable<Photo> {
        return photosInMemoryCache.oneObservable(photoId)
    }

    companion object {
        const val SAVED_SEARCHES = "saved_searches"
    }
}
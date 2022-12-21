package com.cupsofcode.photosearch.features.details

import androidx.lifecycle.ViewModel
import com.cupsofcode.photosearch.repository.PhotoRepository
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import java.util.concurrent.Callable
import javax.inject.Inject


class PhotoDetailsViewModel @Inject constructor(private val repository: PhotoRepository) :
    ViewModel() {

    private val initialState = Callable { PhotoDetailsUIState() }

    private val reducer =
        BiFunction<PhotoDetailsUIState, PhotoDetailsIntent, PhotoDetailsUIState> { previousState, intent ->
            when (intent) {
                is PhotoDetailsIntent.MainContent -> previousState.copy(
                    photo = intent.photo,
                    isLoading = false,
                    error = null
                )
                is PhotoDetailsIntent.Loading -> previousState.copy(isLoading = true)
                is PhotoDetailsIntent.Error -> previousState.copy(
                    isLoading = false,
                    error = intent.throwable
                )
                else -> previousState
            }
        }

    fun bind(intents: Observable<PhotoDetailsIntent>) = bindIntents(intents)
        .scanWith(initialState, reducer)
        .onErrorReturn {
            PhotoDetailsUIState().copy(error = it, isLoading = false)
        }
        .distinctUntilChanged()

    private fun bindIntents(intents: Observable<PhotoDetailsIntent>): Observable<PhotoDetailsIntent> {

        return intents.publish { downstream ->
            val retrievePhoto = downstream.ofType(PhotoDetailsIntent.PhotoIdReceived::class.java)
                .flatMap { intent ->
                    repository.getPhotoDetails(intent.photoId)
                        .map<PhotoDetailsIntent> { photo ->
                            PhotoDetailsIntent.MainContent(photo = photo)
                        }
                }
            val bookmarkToggled =
                downstream.ofType(PhotoDetailsIntent.PhotoBookmarkToggled::class.java)
                    .flatMap { intent ->
                        repository.bookmarkToggled(intent.photoId)
                            .toObservable<PhotoDetailsIntent>()
                    }.startWith(PhotoDetailsIntent.Loading)

            Observable.merge(retrievePhoto, bookmarkToggled)
        }
    }
}
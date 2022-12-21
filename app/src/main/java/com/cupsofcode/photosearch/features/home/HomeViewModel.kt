package com.cupsofcode.photosearch.features.home

import androidx.lifecycle.ViewModel
import com.cupsofcode.photosearch.navigator.Navigator
import com.cupsofcode.photosearch.navigator.NavigatorPath
import com.cupsofcode.photosearch.repository.PhotoRepository
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class HomeViewModel @Inject constructor(
    private val repository: PhotoRepository,
    private val navigator: Navigator
) : ViewModel() {

    private val initialState = Callable { HomeUIState() }

    private val reducer =
        BiFunction<HomeUIState, HomeIntent, HomeUIState> { previousState, intent ->
            when (intent) {
                is HomeIntent.MainContent -> previousState.copy(
                    photos = intent.photos,
                    showLoading = false,
                    error = null,
                    showPreviousSearches = false,
                    showEmptyView = false
                )
                is HomeIntent.Loading -> previousState.copy(
                    showLoading = true,
                    showPreviousSearches = false
                )
                is HomeIntent.Error -> previousState.copy(
                    showLoading = false,
                    error = intent.throwable
                )
                is HomeIntent.ShowSavedSearches -> previousState.copy(
                    savedSearches = intent.searches,
                    showLoading = false,
                    showPreviousSearches = true,
                    showEmptyView = intent.searches.isEmpty()
                )
                is HomeIntent.Search -> previousState.copy(latestSearchQuery = intent.text)
                is HomeIntent.SearchCleared -> previousState.copy(
                    latestSearchQuery = "",
                    showPreviousSearches = true
                )
                else -> previousState
            }
        }

    fun bind(intents: Observable<HomeIntent>) = bindIntents(intents)
        .scanWith(initialState, reducer)
        .onErrorReturn {
            HomeUIState().copy(error = it, showLoading = false)
        }
        .distinctUntilChanged()


    private fun bindIntents(intents: Observable<HomeIntent>): Observable<HomeIntent> {
        val dataIntent = repository.getSearchResults()
            .map<HomeIntent> { photos ->
                HomeIntent.MainContent(photos = photos)
            }
            .onErrorReturn { HomeIntent.Error(throwable = it) }

        val userIntents = intents.publish { downstream ->

            val searchFocused = downstream.ofType(HomeIntent.SearchFocused::class.java)
                .flatMap {
                    repository.getSavedSearches()
                        .map<HomeIntent> {
                            HomeIntent.ShowSavedSearches(it)
                        }.toObservable()
                        .startWith(HomeIntent.Loading)
                        .onErrorReturn { HomeIntent.Error(throwable = it) }
                }

            val photoClicked = downstream.ofType(HomeIntent.PhotoDetailsClicked::class.java)
                .flatMapCompletable {
                    navigator.navigateTo(NavigatorPath.Details(it.photo.id))
                }.toObservable<HomeIntent>()

            val search = downstream.ofType(HomeIntent.Search::class.java)
                .debounce(100, TimeUnit.MILLISECONDS)
                .filter { it.text.isNotEmpty() }
                .switchMap { query ->
                    repository.searchPhotos(searchTerm = query.text)
                        .toObservable<HomeIntent>()
                        .startWith(HomeIntent.Loading)
                        .onErrorReturn { HomeIntent.Error(throwable = it) }
                }

            val removeSearchQuery =
                downstream.ofType(HomeIntent.RemoveSearchQueryClicked::class.java)
                    .flatMap {
                        repository.removeSearchItem(it.query).andThen(
                            repository.getSavedSearches().toObservable()
                                .map<HomeIntent> {
                                    HomeIntent.ShowSavedSearches(it)
                                }

                        ).startWith(HomeIntent.Loading)
                            .onErrorReturn { HomeIntent.Error(throwable = it) }
                    }

            Observable.mergeArray(
                photoClicked,
                search,
                searchFocused,
                removeSearchQuery,
                downstream.ofType(HomeIntent.SearchCleared::class.java)
            )
        }
        return Observable.merge(dataIntent, userIntents)
    }
}
package com.cupsofcode.photosearch.features.home

import com.cupsofcode.photosearch.repository.model.Photo


data class HomeUIState(
    val showLoading: Boolean = false,
    val error: Throwable? = null,
    val photos: List<Photo> = emptyList(),
    val latestSearchQuery: String = "",
    val savedSearches: List<String> = emptyList(),
    val showEmptyView: Boolean = true,
    val showPreviousSearches: Boolean = false
)

sealed class HomeIntent {
    object Loading : HomeIntent()
    data class MainContent(val photos: List<Photo>) : HomeIntent()
    data class Error(val throwable: Throwable) : HomeIntent()
    data class ShowSavedSearches(val searches: List<String>) : HomeIntent()

    object SearchFocused : HomeIntent()
    object SearchCleared : HomeIntent()
    data class Search(val text: String) : HomeIntent()

    data class PhotoDetailsClicked(val photo: Photo) : HomeIntent()
    data class RemoveSearchQueryClicked(val query: String) : HomeIntent()


}


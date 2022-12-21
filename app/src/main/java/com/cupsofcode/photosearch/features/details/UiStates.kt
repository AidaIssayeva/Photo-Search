package com.cupsofcode.photosearch.features.details

import com.cupsofcode.photosearch.repository.model.Photo


data class PhotoDetailsUIState(
    val isLoading: Boolean = true,
    val error: Throwable? = null,
    val photo: Photo? = null,
    val isSaved: Boolean = false
)

sealed class PhotoDetailsIntent {
    object Loading : PhotoDetailsIntent()
    data class Error(val throwable: Throwable) : PhotoDetailsIntent()
    data class MainContent(val photo: Photo) : PhotoDetailsIntent()

    data class PhotoIdReceived(val photoId: String) : PhotoDetailsIntent()
    data class PhotoBookmarkToggled(val photoId: String) : PhotoDetailsIntent()
}
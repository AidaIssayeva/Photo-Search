package com.cupsofcode.photosearch.repository.model

import com.cupsofcode.photosearch.network.PhotoResponse

fun PhotoResponse.toPhoto(): Photo {
    return Photo(
        id = id,
        imageUri = imageUri,
        title = title,
        isSaved = false // TODO: update when photo bookmarked
    )
}
package com.cupsofcode.photosearch

import com.cupsofcode.photosearch.repository.model.Photo

val singlePhoto: Photo = Photo(
    id = "1",
    title = "Beautiful photo",
    imageUri = "link to the photo",
    isSaved = false
)

val photos = listOf<Photo>(
    singlePhoto,
//    singlePhoto.copy(id = 2),
//    singlePhoto.copy(id = 3),
//    singlePhoto.copy(id = 4)
)

val savedSearchTerms = listOf<String>(
    "ice",
    "snow",
    "winter"
)
package com.cupsofcode.photosearch.repository.model


class Photo(
    val id: String,
    val title: String,
    val imageUri: String,
    val isSaved: Boolean
)
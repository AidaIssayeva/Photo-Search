package com.cupsofcode.photosearch.network


data class PhotosResponse(val photos: PhotosCollectionResponse)

data class PhotosCollectionResponse(
    val page: Int,
    val pages: Int,
    val perpage: Int,
    val total: Int,
    val photo: List<PhotoResponse>
)

data class PhotoResponse(
    val id: String,
    val owner: String,
    val secret: String,
    val server: String,
    val title: String,
    val ispublic: String
) {
    val imageUri: String
        get() = "https://farm1.staticflickr.com/$server/${id}_$secret.jpg"
}
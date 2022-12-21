package com.cupsofcode.photosearch.navigator


sealed class NavigatorPath {
    object Feed : NavigatorPath()
    data class Details(val id: String) : NavigatorPath()
}
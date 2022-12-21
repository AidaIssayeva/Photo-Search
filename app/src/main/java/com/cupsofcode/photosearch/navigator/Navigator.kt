package com.cupsofcode.photosearch.navigator

import io.reactivex.Completable

interface Navigator {
    fun navigateTo(path: NavigatorPath): Completable
}
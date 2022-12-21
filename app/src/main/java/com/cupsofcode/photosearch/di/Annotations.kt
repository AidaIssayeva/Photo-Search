package com.cupsofcode.photosearch.di

import javax.inject.Qualifier
import javax.inject.Scope


@Qualifier
annotation class BaseUrl

@Qualifier
annotation class APIKey

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope
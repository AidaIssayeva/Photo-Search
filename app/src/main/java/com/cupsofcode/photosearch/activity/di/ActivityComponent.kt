package com.cupsofcode.photosearch.activity.di

import com.cupsofcode.photosearch.di.ActivityScope
import com.cupsofcode.photosearch.features.details.PhotoDetailsViewModel
import com.cupsofcode.photosearch.features.home.HomeViewModel
import com.cupsofcode.photosearch.navigator.Navigator
import com.cupsofcode.photosearch.activity.di.DaggerActivityComponent
import com.cupsofcode.photosearch.repository.di.PhotosComponent
import dagger.Component


@Component(
    modules = [ActivityModule::class],
    dependencies = [PhotosComponent::class]
)
@ActivityScope
interface ActivityComponent {

    fun homeViewModel(): HomeViewModel

    fun photoDetailsViewModel(): PhotoDetailsViewModel

    fun navigator(): Navigator

    @Component.Builder
    interface Builder {
        fun module(navigatorModule: ActivityModule): Builder
        fun photoComponent(component: PhotosComponent): Builder
        fun build(): ActivityComponent
    }

    companion object {
        fun builder(): Builder {
            return DaggerActivityComponent.builder()
        }
    }
}
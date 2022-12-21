package com.cupsofcode.photosearch.activity.di

import androidx.fragment.app.FragmentManager
import com.cupsofcode.photosearch.activity.MainActivity
import com.cupsofcode.photosearch.di.ActivityScope
import com.cupsofcode.photosearch.navigator.Navigator
import com.cupsofcode.photosearch.navigator.NavigatorImpl
import dagger.Module
import dagger.Provides


@Module
class ActivityModule(private val activity: MainActivity) {

    @Provides
    @ActivityScope
    fun providesNavigator(fragmentManager: FragmentManager): Navigator {
        return NavigatorImpl(fragmentManager)
    }

    @Provides
    fun providesFragmentManager(): FragmentManager {
        return activity.supportFragmentManager
    }
}
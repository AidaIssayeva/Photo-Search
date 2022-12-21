package com.cupsofcode.photosearch.navigator

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.cupsofcode.photosearch.R
import com.cupsofcode.photosearch.features.details.PhotoDetailsFragment
import com.cupsofcode.photosearch.features.home.HomeFragment
import io.reactivex.Completable
import javax.inject.Inject


class NavigatorImpl @Inject constructor(
    private val fragmentManager: FragmentManager
) : Navigator {

    override fun navigateTo(path: NavigatorPath): Completable {
        return Completable.fromAction {
            val fragment: Fragment = when (path) {
                is NavigatorPath.Feed -> HomeFragment.newInstance()
                is NavigatorPath.Details -> PhotoDetailsFragment.newInstance(path.id)
            }

            fragmentManager
                .beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack(fragment::class.java.name)
                .commit()
        }

    }
}
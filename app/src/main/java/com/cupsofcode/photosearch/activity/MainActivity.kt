package com.cupsofcode.photosearch.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cupsofcode.photosearch.PhotoSearchApplication
import com.cupsofcode.photosearch.databinding.ActivityMainBinding
import com.cupsofcode.photosearch.navigator.NavigatorPath
import com.cupsofcode.photosearch.activity.di.ActivityComponent
import com.cupsofcode.photosearch.activity.di.ActivityModule

class MainActivity : AppCompatActivity() {
    private var component: ActivityComponent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        component = getActivityComponent()
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getActivityComponent().navigator().navigateTo(NavigatorPath.Feed)
            .subscribe({

            }, {

            }).dispose()
    }

    fun getActivityComponent(): ActivityComponent {
        return component ?: ActivityComponent.builder()
            .module(ActivityModule(activity = this))
            .photoComponent((application as PhotoSearchApplication).component)
            .build()
    }

    override fun onBackPressed() {
        val fragments = supportFragmentManager.backStackEntryCount
        if (fragments == 1) {
            finish()
        } else {
            if (fragments > 1) {
                supportFragmentManager.popBackStack()
            } else {
                super.onBackPressed()
            }
        }
    }
}
package com.cupsofcode.photosearch

import com.cupsofcode.photosearch.features.home.HomeIntent
import com.cupsofcode.photosearch.features.home.HomeUIState
import com.cupsofcode.photosearch.features.home.HomeViewModel
import com.cupsofcode.photosearch.navigator.Navigator
import com.cupsofcode.photosearch.navigator.NavigatorPath
import com.cupsofcode.photosearch.repository.PhotoRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit


class HomeViewModelTest {
    @RelaxedMockK
    private lateinit var repository: PhotoRepository

    @RelaxedMockK
    private lateinit var navigator: Navigator

    private val testScheduler = TestScheduler()
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var intentSubject: PublishSubject<HomeIntent>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }
        homeViewModel = HomeViewModel(repository, navigator)
        intentSubject = PublishSubject.create<HomeIntent>()
    }

    @Test
    fun `should return an initial viewState when viewmodel is binded`() {
        //given the initialized viewmodel
        val expectedResult = HomeUIState()

        //when it gets binded
        val resultsObserver = homeViewModel.bind(intentSubject).test()

        //then
        resultsObserver.assertValue(expectedResult)
    }

    @Test
    fun `should return a viewstate filled with the list of photos when user searches`() {
        //given
        val expectedResult = arrayOf(
            HomeUIState(),
            HomeUIState(showLoading = true),
            HomeUIState(
                photos = photos, showLoading = false, showPreviousSearches = false,
                showEmptyView = false
            )
        )
        val searchText = "sunshine"

        every { repository.searchPhotos(searchTerm = searchText) } returns Observable.just(photos)

        // when
        val resultsObserver = homeViewModel.bind(intentSubject).test()
        intentSubject.onNext(HomeIntent.Search(text = searchText))

        // then
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        resultsObserver.assertValues(*expectedResult)
    }

    @Test
    fun `should return a viewstate with error when user searches and response is not successfull `() {
        //given
        val error = Throwable("error")
        val expectedResult = arrayOf(
            HomeUIState(),
            HomeUIState(showLoading = true),
            HomeUIState(error = error, showLoading = false)
        )
        val searchText = "sunshine"
        every { repository.searchPhotos(searchTerm = searchText) } returns Observable.error(error)

        //when
        val resultsObserver = homeViewModel.bind(intentSubject).test()
        intentSubject.onNext(HomeIntent.Search(text = searchText))

        //then
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        resultsObserver.assertValues(*expectedResult)
    }

    @Test
    fun `should return a viewstate with saved searches when user focuses on search view`() {
        //given
        val expectedResult = arrayOf(
            HomeUIState(),
            HomeUIState(showLoading = true),
            HomeUIState(
                savedSearches = savedSearchTerms,
                showLoading = false,
                showPreviousSearches = true,
                showEmptyView = savedSearchTerms.isEmpty()
            )
        )

        every { repository.getSavedSearches() } returns Single.just(savedSearchTerms)

        //when
        val resultsObserver = homeViewModel.bind(intentSubject).test()
        intentSubject.onNext(HomeIntent.SearchFocused)

        //then
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        resultsObserver.assertValues(*expectedResult)
    }

    @Test
    fun `should return a viewstate with saved searches when user clicks on X on search view`() {
        //given
        val expectedResult = arrayOf(
            HomeUIState(),
            HomeUIState(
                latestSearchQuery = "",
                showPreviousSearches = true
            )
        )

        //when
        val resultsObserver = homeViewModel.bind(intentSubject).test()
        intentSubject.onNext(HomeIntent.SearchCleared)

        //then
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        resultsObserver.assertValues(*expectedResult)
    }

    @Test
    fun `should navigate to details when photo clicked`() {
        //given
        val expectedPhoto = singlePhoto
        //when
        homeViewModel.bind(intentSubject).test()
        intentSubject.onNext(HomeIntent.PhotoDetailsClicked(expectedPhoto))

        //then
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        verify(exactly = 1) {
            navigator.navigateTo(NavigatorPath.Details(id = expectedPhoto.id))
        }
    }

    @Test
    fun `should return a viewstate with less saved searches when user deletes one of the search items from recently saved searches`() {
        //given
        val expectedTermToRemove = savedSearchTerms[0]
        val expectedNewList = savedSearchTerms.filter { it == expectedTermToRemove }
        val expectedResult = arrayOf(
            HomeUIState(),
            HomeUIState(showLoading = true),
            HomeUIState(
                savedSearches = expectedNewList,
                showLoading = false,
                showPreviousSearches = true,
                showEmptyView = expectedNewList.isEmpty()
            )
        )
        every { repository.removeSearchItem(expectedTermToRemove) } returns Completable.complete()
        every { repository.getSavedSearches() } returns Single.just(expectedNewList)

        //when
        val resultsObserver = homeViewModel.bind(intentSubject).test()
        intentSubject.onNext(HomeIntent.RemoveSearchQueryClicked(expectedTermToRemove))

        //then
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        resultsObserver.assertValues(*expectedResult)
    }


}
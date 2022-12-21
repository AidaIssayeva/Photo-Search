package com.cupsofcode.photosearch

import org.junit.Before
import org.junit.Test
import com.cupsofcode.photosearch.utils.InMemoryCache

class InMemoryCacheUnitTest {
    private lateinit var inMemoryCache: InMemoryCache<Pair<String, Int>>
    private lateinit var listOfString: ArrayList<Pair<String, Int>>
    @Before
    fun setupGlobalCache() {
        inMemoryCache = InMemoryCache()
        listOfString = arrayListOf(
            "photo" to 0,
            "android" to 1,
            "2022" to 2,
            "app" to 3,
            "search" to 4,
            "this" to 5
        )
    }

    @Test
    fun testOneValueSingleExists() {
        //id exists
        inMemoryCache.putAll(listOfString) { it.first }.andThen(inMemoryCache.oneValueSingle("2022"))
            .test().assertResult("2022" to 2)
    }

    @Test
    fun testOneValueSingleNotExist() {
        //id doesn't exists
        inMemoryCache.putAll(listOfString) { it.first }
            .andThen(inMemoryCache.oneValueSingle("2018"))
            .test()
            .assertError(NoSuchElementException::class.java)

    }

    @Test
    fun testOneValueObservableNotExist() {
        //id doesn't exists
        inMemoryCache.putAll(listOfString) { it.first }
            .andThen(inMemoryCache.oneValueObservable("2018"))
            .test()
            .assertEmpty()
    }

    @Test
    fun oneValueObservableDidNotExistThenAdded() {
        inMemoryCache.putAll(listOfString) { it.first }.blockingAwait()

        val testObs = inMemoryCache.oneValueObservable("2018")
            .test()
            .assertEmpty()

        inMemoryCache.put("2018" to 6, "2018").blockingAwait()

        testObs.assertValues("2018" to 6)

    }

    @Test
    fun oneValueObservableChangedBeforeAddingAllValues() {
        val testObs = inMemoryCache.oneValueObservable("2022").test()

        inMemoryCache.putAll(listOfString) { it.first }.blockingAwait()

        inMemoryCache.put("2022" to 6, "2022").blockingAwait()

        testObs.assertValues("2022" to 2, "2022" to 6)
    }

    @Test
    fun oneValueObservableChangedAfterAddingAllValues() {
        inMemoryCache.putAll(listOfString) { it.first }.blockingAwait()

        val testObs = inMemoryCache.oneValueObservable("2022").test()
            .assertValue("2022" to 2)

        inMemoryCache.put("2022" to 6, "2022").blockingAwait()

        testObs.assertValues("2022" to 2, "2022" to 6)

    }

    @Test
    fun testOneValuesObservableExists() {
        inMemoryCache.putAll(listOfString) { it.first }
            .andThen(inMemoryCache.oneValueObservable("2022")).test().assertValue("2022" to 2)

    }

    @Test
    fun testAllValuesObservableExists() {
        inMemoryCache.putAll(listOfString) { it.first }
            .andThen(inMemoryCache.allValuesObservable())
            .map { it.sortedBy { it.second } }
            .test().assertValues(listOfString)
    }

    @Test
    fun testAllValueSingleExists() {
        inMemoryCache.putAll(listOfString) { it.first }
            .andThen(inMemoryCache.allValuesSingle())
            .map { it.sortedBy { it.second } }
            .test().assertValues(listOfString)

    }

}
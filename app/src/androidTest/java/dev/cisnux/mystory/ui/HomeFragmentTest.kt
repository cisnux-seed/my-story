package dev.cisnux.mystory.ui

import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.cisnux.mystory.DataDummy.TESTING_TOKEN
import dev.cisnux.mystory.EspressoIdlingResource
import dev.cisnux.mystory.R
import dev.cisnux.mystory.launchFragmentInHiltContainer
import dev.cisnux.mystory.locals.AuthLocalDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@MediumTest
@ExperimentalCoroutinesApi
@HiltAndroidTest
class HomeFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var localDataSource: AuthLocalDataSource

    @Before
    fun setUp() = runTest {
        hiltRule.inject()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        localDataSource.saveToken(TESTING_TOKEN)
    }

    @After
    fun tearDown() = runTest {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        localDataSource.deleteToken()
    }

    @Test
    fun logout_Success() {
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )

        launchFragmentInHiltContainer<HomeFragment>(
            themeResId = R.style.Theme_MyStory,
            navController = navController
        )

        onView(withId(R.id.action_logout))
            .check(ViewAssertions.matches(isDisplayed()))
            .perform(click())

        assertEquals(R.id.loginFragment, navController.currentDestination?.id)
    }

    @Test
    fun loadDetailStory_Success() {
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )

        launchFragmentInHiltContainer<HomeFragment>(
            themeResId = R.style.Theme_MyStory,
            navController = navController
        )

        onView(withId(R.id.storyRecyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click()
            )
        )

        assertEquals(R.id.detailFragment, navController.currentDestination?.id)
    }
}
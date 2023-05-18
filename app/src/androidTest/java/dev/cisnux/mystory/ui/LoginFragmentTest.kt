package dev.cisnux.mystory.ui

import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressBack
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.cisnux.mystory.DataDummy.EMAIL
import dev.cisnux.mystory.DataDummy.PASSWORD
import dev.cisnux.mystory.EspressoIdlingResource
import dev.cisnux.mystory.R
import dev.cisnux.mystory.launchFragmentInHiltContainer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MediumTest
@ExperimentalCoroutinesApi
@HiltAndroidTest
class LoginFragmentTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun login_Success() {
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )
        launchFragmentInHiltContainer<LoginFragment>(
            themeResId = R.style.Theme_MyStory,
            navController = navController
        )
        onView(withId(R.id.emailEditText))
            .check(matches(isDisplayed()))
            .perform(
                typeText(EMAIL)
            )
        onView(withId(R.id.passwordEditText))
            .check(matches(isDisplayed()))
            .perform(
                typeText(PASSWORD),
                pressBack()
            )
        onView(withId(R.id.loginButton))
            .check(matches(isDisplayed()))
            .perform(click())

        assertEquals(R.id.homeFragment, navController.currentDestination?.id)
    }
}
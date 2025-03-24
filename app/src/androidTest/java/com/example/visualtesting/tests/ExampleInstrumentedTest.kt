package com.example.visualtesting.tests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.visualtesting.R
import com.example.visualtesting.TestListener
import com.example.visualtesting.utils.TestUtilities
import com.example.visualtesting.utils.VisualAssert.assertCompareImages
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest : BaseTest() {

    @get:Rule
    val watcher = TestListener()

    val uiTestsUtils = TestUtilities()

    @Before
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.visualtesting", appContext.packageName)
    }

    @Test
    fun testImagesDoNotMatch() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        onView(withText("Question"))
            .check(matches(isDisplayed()))
        onView(withText("Response"))
            .check(matches(isDisplayed()))

        val actualScreenshotFilePath = uiTestsUtils.takeTheScreenshot(appContext, "TestResult")
        println("actualScreenshotFilePath=======" + actualScreenshotFilePath)
        val expectedResId = R.drawable.sample2

        assertCompareImages(actualScreenshotFilePath, expectedResId, appContext)
    }

    @Test
    fun testImagesMatch() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        onView(withText("Question"))
            .check(matches(isDisplayed()))
        onView(withText("Response"))
            .check(matches(isDisplayed()))

        val actualScreenshotFilePath = uiTestsUtils.takeTheScreenshot(appContext, "TestResult")
        println("actualScreenshotFilePath=======" + actualScreenshotFilePath)
        val expectedResId = R.drawable.expected

        assertCompareImages(actualScreenshotFilePath, expectedResId, appContext)
    }

    @Test
    fun testImagesDoNotMatchFilureSecond() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        onView(withText("Question"))
            .check(matches(isDisplayed()))
        onView(withText("Response"))
            .check(matches(isDisplayed()))

        val actualScreenshotFilePath = uiTestsUtils.takeTheScreenshot(appContext, "TestResult")
        println("actualScreenshotFilePath=======" + actualScreenshotFilePath)
        val expectedResId = R.drawable.sample2

        assertCompareImages(actualScreenshotFilePath, expectedResId, appContext)
    }
}

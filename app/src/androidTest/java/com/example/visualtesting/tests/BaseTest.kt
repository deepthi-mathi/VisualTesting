package com.example.visualtesting.tests

import android.Manifest
import androidx.core.app.ActivityCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import com.example.visualtesting.MainActivity
import org.junit.Before as BeforeTest
import org.junit.After as AfterTest

open class BaseTest {

    lateinit var scenario: ActivityScenario<MainActivity>

    @BeforeTest
    open fun setUp() {
        scenario = launch(MainActivity::class.java)

        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        // Launch the activity with permission requests
        scenario.onActivity { activity ->
            ActivityCompat.requestPermissions(activity, permissions, 1)
        }
    }

    @AfterTest
    open fun tearDown() {
        if (::scenario.isInitialized) {
            scenario.close()
        }
    }
}

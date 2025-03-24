package com.example.visualtesting

import org.junit.rules.TestWatcher
import org.junit.runner.Description

object TestMethodHolder {
    var currentTestMethodName: String = "UnknownTest"
}

class TestListener : TestWatcher() {
    override fun starting(description: Description) {
        super.starting(description)
        TestMethodHolder.currentTestMethodName = description.methodName
    }

    override fun finished(description: Description) {
        super.finished(description)
        TestMethodHolder.currentTestMethodName = "UnknownTest" // Reset after test
    }
}

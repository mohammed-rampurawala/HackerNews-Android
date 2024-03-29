package com.mr.myapplication

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class HackerTestRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, TestHackerApplication::class.java.name, context)
    }
}
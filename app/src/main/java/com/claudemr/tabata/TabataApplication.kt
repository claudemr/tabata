package com.claudemr.tabata

import android.app.Application

class TabataApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        container = AppContainer(applicationContext)

        container.speechManager.initialize()
    }

    override fun onTerminate() {
        super.onTerminate()

        container.speechManager.shutdown()
    }
}
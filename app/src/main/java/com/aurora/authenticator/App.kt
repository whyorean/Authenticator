package com.aurora.authenticator

import android.app.Application
import nl.komponents.kovenant.android.startKovenant
import nl.komponents.kovenant.android.stopKovenant

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKovenant()
    }

    override fun onTerminate() {
        stopKovenant()
        super.onTerminate()
    }
}

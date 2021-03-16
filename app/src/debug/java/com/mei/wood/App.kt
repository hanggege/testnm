package com.mei.wood

import com.mei.init.BaseApp
import com.mei.orc.util.app.setEnabledBlocking

class App : BaseApp() {

    override fun onCreate() {
        super.onCreate()
        setEnabledBlocking(applicationContext, true)
    }
}
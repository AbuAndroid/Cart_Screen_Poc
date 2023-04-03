package com.example.foodorderscreenpoc

import android.app.Application
import com.example.foodorderscreenpoc.di.module.Test
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FoodOrderScreenMain:Application() {

    override fun onCreate() {
        super.onCreate()
        config()
    }

    private fun config() {
        startKoin{
            androidContext(this@FoodOrderScreenMain)
            modules(Test.modules())
        }
    }
}
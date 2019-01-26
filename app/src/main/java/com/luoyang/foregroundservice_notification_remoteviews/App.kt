package com.luoyang.foregroundservice_notification_remoteviews

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ToastUtil.init(this)
    }
}
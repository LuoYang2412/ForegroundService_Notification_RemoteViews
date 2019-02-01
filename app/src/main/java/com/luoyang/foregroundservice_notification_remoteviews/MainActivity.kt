package com.luoyang.foregroundservice_notification_remoteviews

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(view: View) {
        when (view) {
            sendNotification_button -> {
                val myMusicServiceIntent = Intent(this, MyMusicService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(myMusicServiceIntent)
                } else {
                    startService(myMusicServiceIntent)
                }
            }
        }
    }
}

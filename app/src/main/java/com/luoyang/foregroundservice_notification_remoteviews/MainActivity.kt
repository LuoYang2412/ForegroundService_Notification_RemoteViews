package com.luoyang.foregroundservice_notification_remoteviews

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(view: View) {
        when (view) {
            sendNotification_button -> {
                val channelId = "musicPlay"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channelName = "音乐播放"
                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                    createNotificationChannel(channelId, channelName, importance)
                }
                sendNotification(R.layout.layout_play_music_notification, channelId)
            }
        }
    }

    /**
     * Android 8.0 + 创建通知渠道
     */
    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String, importance: Int) {
        val notificationChannel = NotificationChannel(channelId, channelName, importance)
        val notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationManagerCompat.createNotificationChannel(notificationChannel)
    }

    val ACTION_NOTIFICATION = "ACTION_NOTIFICATION"
    val BUTTON_INDEX = "BUTTON_INDEX"
    val BUTTON_PREV = "BUTTON_PREV"
    val BUTTON_PLAY = "BUTTON_PLAY"
    val BUTTON_NEXT = "BUTTON_NEXT"

    /**
     * 发送通知
     * @param layoutId 布局
     * @param channelId 渠道
     */
    private fun sendNotification(@LayoutRes layoutId: Int, channelId: String) {
        val notificationManagerCompat = NotificationManagerCompat.from(this)
        //检查渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = notificationManagerCompat.getNotificationChannel(channelId)
            if (notificationChannel?.importance == NotificationManagerCompat.IMPORTANCE_NONE) {
                val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, notificationChannel.id)
                startActivity(intent)
                ToastUtil.show("需要开启通知渠道")
            }
        }
        //构建remoteViews
        val remoteViews = RemoteViews(packageName, layoutId)
        remoteViews.apply {
            setImageViewResource(
                R.id.musicIcon_imageView,
                R.drawable.ic_launcher_foreground
            )
            setTextViewText(R.id.musicName_textView, "三国杀-汪苏泷")
            setTextViewText(R.id.playTime_textView, "1:30")
            setTextViewText(R.id.musicTime_textView, "3:25")
        }
        val intent = Intent(this, this::class.java)
        intent.action = ACTION_NOTIFICATION

        intent.putExtra(BUTTON_INDEX, BUTTON_PREV)
        var pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        remoteViews.setOnClickPendingIntent(R.id.imageButton2, pendingIntent)

        intent.putExtra(BUTTON_INDEX, BUTTON_PLAY)
        pendingIntent = PendingIntent.getActivity(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        remoteViews.setOnClickPendingIntent(R.id.imageButton3, pendingIntent)

        intent.putExtra(BUTTON_INDEX, BUTTON_NEXT)
        pendingIntent = PendingIntent.getActivity(this, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        remoteViews.setOnClickPendingIntent(R.id.imageButton4, pendingIntent)

        //发送通知
        val notification = NotificationCompat.Builder(this, channelId).apply {
            setContent(remoteViews)
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setContentIntent(
                PendingIntent.getActivity(
                    this@MainActivity,
                    0,
                    Intent(this@MainActivity, this@MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        }.build()
        notificationManagerCompat.notify(1, notification)
    }
}

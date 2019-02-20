package com.luoyang.foregroundservice_notification_remoteviews

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * 音乐播放服务
 */
class MyMusicService : Service() {

    val ACTION_NOTIFICATION = "ACTION_NOTIFICATION"
    val BUTTON_INDEX = "BUTTON_INDEX"
    val BUTTON_PREV = "BUTTON_PREV"
    val BUTTON_PLAY = "BUTTON_PLAY"
    val BUTTON_NEXT = "BUTTON_NEXT"
    val BUTTON_CLOSE = "BUTTON_CLOSE"

    val channelId = "musicPlay"

    lateinit var mRemoteViews: RemoteViews
    lateinit var notificationManagerCompat: NotificationManagerCompat
    lateinit var mMediaPlayer: MediaPlayer

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        notificationManagerCompat = NotificationManagerCompat.from(this)
        initRemoteViews()
        createNotificationChannel()
        checkNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_NOTIFICATION) {
            when (intent.getStringExtra(BUTTON_INDEX)) {
                BUTTON_PREV -> {

                }
                BUTTON_PLAY -> {

                }
                BUTTON_NEXT -> {

                }
                BUTTON_CLOSE -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        stopForeground(Service.STOP_FOREGROUND_REMOVE)
                    } else {
                        stopSelf()
                    }
                }
            }
        } else {
            sendNotification()
        }
        return START_REDELIVER_INTENT
    }

    /**
     * 发送通知
     */
    private fun sendNotification() {
        val notification = NotificationCompat.Builder(this, channelId)
            .apply {
                setContent(mRemoteViews)
                setSmallIcon(R.drawable.ic_launcher_foreground)
                setContentIntent(
                    PendingIntent.getActivity(
                        this@MyMusicService,
                        0,
                        Intent(this@MyMusicService, MainActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
                setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                setAutoCancel(false)
                setOngoing(true)
            }
            .build()
//        notificationManagerCompat.notify(1, notification)
        startForeground(1, notification)
    }

    /**
     * 检查通知渠道
     */
    private fun checkNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = notificationManagerCompat.getNotificationChannel(channelId)
            if (notificationChannel?.importance == NotificationManagerCompat.IMPORTANCE_NONE) {
                val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, notificationChannel.id)
                startActivity(intent)
                ToastUtil.show("需要开启音乐播放通知渠道")
                return
            }
        }
    }

    /**
     * 初始化RemoteViews
     */
    private fun initRemoteViews() {
        mRemoteViews = RemoteViews(packageName, R.layout.layout_play_music_notification)
        mRemoteViews.apply {
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
        //设置上一个点击事件
        intent.putExtra(BUTTON_INDEX, BUTTON_PREV)
        var pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        mRemoteViews.setOnClickPendingIntent(R.id.imageButton2, pendingIntent)
        //设置播放点击事件
        intent.putExtra(BUTTON_INDEX, BUTTON_PLAY)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pendingIntent = PendingIntent.getForegroundService(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            pendingIntent = PendingIntent.getService(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        mRemoteViews.setOnClickPendingIntent(R.id.imageButton3, pendingIntent)
        //设置下一个点击事件
        intent.putExtra(BUTTON_INDEX, BUTTON_NEXT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pendingIntent = PendingIntent.getForegroundService(this, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            pendingIntent = PendingIntent.getService(this, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        mRemoteViews.setOnClickPendingIntent(R.id.imageButton4, pendingIntent)
        //设置关闭点击事件
        intent.putExtra(BUTTON_INDEX, BUTTON_CLOSE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pendingIntent = PendingIntent.getForegroundService(this, 4, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            pendingIntent = PendingIntent.getService(this, 4, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        mRemoteViews.setOnClickPendingIntent(R.id.imageButton5, pendingIntent)
    }

    /**
     * Android 8.0 + 创建通知渠道
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "音乐播放"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationManagerCompat.createNotificationChannel(notificationChannel)
        }
    }
}

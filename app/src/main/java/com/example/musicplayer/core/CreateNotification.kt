package com.example.musicplayer.core

import android.app.Notification
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.musicplayer.R
import com.example.musicplayer.domain.model.AudioModel


object CreateNotification {
    const val CHANNEL_ID = "channel1"
    const val CHANNEL_PLAY = "actionPlay"
    const val CHANNEL_PREVIOUS = "actionPrevious"
    const val CHANNEL_NEXT = "actionNext"
    const val NOTIFICATION_ID = 1

    lateinit var notification: Notification

    fun createNotification(context: Context, song: AudioModel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = NotificationManagerCompat.from(context)
            val mediaSessionCompat = MediaSessionCompat(context, "tag")

            val albumUri = Uri.parse("content://media/external/audio/albumart")
            val uri = song.cover?.let { ContentUris.withAppendedId(albumUri, it.toLong()) }

            var icon: Bitmap? = null
            Glide.with(context)
                .asBitmap()
                .load(uri)
                .error(R.drawable.unknown_song)
                .centerCrop()
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        icon = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                })


            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(song.title)
                .setContentText(song.artist)
                .setLargeIcon(icon)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()

            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }
}
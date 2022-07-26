package com.example.musicplayer.core

import android.media.MediaPlayer

object MediaPlayerHelper {
    private val mediaPlayer = MediaPlayer()
    fun getInstance() = mediaPlayer
}
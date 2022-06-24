package com.example.musicplayer.core

import android.media.MediaPlayer

object MediaPlayerHelper {
    var currentIndex = -1
    fun getInstance() = MediaPlayer()
}
package com.example.musicplayer.core

import android.media.MediaPlayer
import com.example.musicplayer.domain.model.AudioModel

object MediaPlayerHelper {
    var currentIndex = -1
    var songsList = emptyList<AudioModel>()
    val mediaPlayer = MediaPlayer()
    fun getInstance() = mediaPlayer
}
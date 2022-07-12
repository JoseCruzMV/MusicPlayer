package com.example.musicplayer.core

import com.example.musicplayer.domain.model.AudioModel

object QueueHelper {
    var currentIndex = - 1
    var songsList = emptyList<AudioModel>()
}
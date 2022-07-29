package com.example.musicplayer.domain.model

import java.io.Serializable

data class AudioModel(
    val path: String,
    val title: String,
    val duration: String,
    val artist: String?,
    val cover: String?,
) : Serializable
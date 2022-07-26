package com.example.musicplayer.ui.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.musicplayer.core.MediaPlayerHelper
import com.example.musicplayer.core.QueueHelper
import com.example.musicplayer.domain.model.AudioModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

class SongsPlayerViewModel : ViewModel() {

    val currentSong = MutableLiveData<AudioModel>()
    private lateinit var _currentSong: AudioModel
    val isSongPlaying = MutableLiveData<Boolean>()


    private val mediaPlayer = MediaPlayerHelper.getInstance()

    fun getCurrentSong() {
        _currentSong = QueueHelper.songsList[QueueHelper.currentIndex]
        currentSong.postValue(_currentSong)
        playMusic()

        CoroutineScope(IO).launch {
            launch { getSongProgression() }
            delay(1000)
        }
    }

    private fun playMusic() {
        mediaPlayer.reset()

        try {
            mediaPlayer.setDataSource(_currentSong.path)
            mediaPlayer.prepare()
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener { nextSong() }
        } catch (e: Exception) {
            Log.d("Player", e.toString())
        }
    }

    fun nextSong() {
        if (QueueHelper.currentIndex == QueueHelper.songsList.lastIndex) {
            QueueHelper.currentIndex = 0
        } else {
            QueueHelper.currentIndex += 1
        }
        mediaPlayer.reset()
        getCurrentSong()
    }

    fun previousSong() {
        if (QueueHelper.currentIndex == 0) {
            QueueHelper.currentIndex = QueueHelper.songsList.lastIndex
        } else {
            QueueHelper.currentIndex -= 1
        }
        mediaPlayer.reset()
        getCurrentSong()
    }

    fun pausePlay() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            isSongPlaying.postValue(false)
        } else {
            mediaPlayer.start()
            isSongPlaying.postValue(true)
        }
    }

    fun getSongProgression() = mediaPlayer.currentPosition

    fun seekSongTo(moveTo: Int) = mediaPlayer.seekTo(moveTo)
}
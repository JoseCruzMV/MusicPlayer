package com.example.musicplayer.ui.viewmodel

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.*
import com.example.musicplayer.core.QueueHelper
import com.example.musicplayer.domain.model.AudioModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class SongsPlayerViewModel @Inject constructor(
    private val mediaPlayer: MediaPlayer,
) : ViewModel() {

    val currentSong = MutableLiveData<AudioModel>()
    private lateinit var _currentSong: AudioModel
    val isSongPlaying = MutableLiveData<Boolean>()
    val songProgress = MutableLiveData<Int> ()

    fun getCurrentSong() {
        _currentSong = QueueHelper.songsList[QueueHelper.currentIndex]
        currentSong.value = _currentSong
        playMusic()
        viewModelScope.launch { getCurrentPosition() }
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

    fun seekSongTo(moveTo: Int) = mediaPlayer.seekTo(moveTo)

    private suspend fun getCurrentPosition() {
        while (true) {
            songProgress.postValue(mediaPlayer.currentPosition)
            delay(1000)
        }
    }
}
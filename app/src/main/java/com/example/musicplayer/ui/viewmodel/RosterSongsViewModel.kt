package com.example.musicplayer.ui.viewmodel

import android.app.Application
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.core.QueueHelper
import com.example.musicplayer.domain.model.AudioModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RosterSongsViewModel @Inject constructor(
    private val application: Application,
) : ViewModel() {
    val songsList = MutableLiveData<List<AudioModel>>()
    private val songs = mutableListOf<AudioModel>()

    fun getSongsList() {
        viewModelScope.launch {
            if (songs.isNotEmpty()) songs.clear()

            val projection = arrayOf(
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
            )

            val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

            val cursor =  application.applicationContext.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null
            )

            cursor?.let {
                while (it.moveToNext()){
                    val songData = AudioModel(
                        title = it.getString(0),
                        path = it.getString(1),
                        duration = it.getString(2)
                    )
                    if (File(songData.path).exists()) songs.add(songData)
                }
            }
            cursor?.close()

            songsList.postValue(songs)
            QueueHelper.songsList = songs
        }
    }
}
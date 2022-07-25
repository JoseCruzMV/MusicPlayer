package com.example.musicplayer.ui.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.musicplayer.R
import com.example.musicplayer.core.MediaPlayerHelper
import com.example.musicplayer.core.QueueHelper
import com.example.musicplayer.databinding.SongsPlayerFragmentBinding
import com.example.musicplayer.domain.model.AudioModel
import java.io.IOException

class SongsPlayerFragment : Fragment() {
    private lateinit var binding: SongsPlayerFragmentBinding
    private val mediaPlayer = MediaPlayerHelper.getInstance()
    private lateinit var currentSong: AudioModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = SongsPlayerFragmentBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            ivNext.setOnClickListener { playNextSong() }
            ivPrevious.setOnClickListener { playPreviousSong() }
            ivPausePlay.setOnClickListener { pausePlay() }

            sbProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (mediaPlayer.isPlaying && fromUser)
                        mediaPlayer.seekTo(progress)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}

                override fun onStopTrackingTouch(p0: SeekBar?) {}

            })
        }
        setResourcesWithMusic()



        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    binding.apply {
                        sbProgress.progress = mediaPlayer.currentPosition
                        tvCurrentTime.text = convertMilliToMMSS(mediaPlayer.currentPosition.toString())

                    }
                    handler.postDelayed(this, 1000)
                } catch (e: Exception) {
                    binding.sbProgress.progress = 0
                }
            }
        }, 0)

    }

    private fun setResourcesWithMusic() {
        //currentSong = MediaPlayerHelper.songsList[MediaPlayerHelper.currentIndex]
        currentSong = QueueHelper.songsList[QueueHelper.currentIndex]
        binding.apply {
            tvPlayerTitle.text = currentSong.title
            tvTotalTime.text = convertMilliToMMSS(currentSong.duration)
            ivPausePlay.setImageResource(R.drawable.ic_pause)
        }
        playMusic()
    }

    private fun playMusic() {
        mediaPlayer.reset()

        try {
            mediaPlayer.setDataSource(currentSong.path)
            mediaPlayer.prepare()
            mediaPlayer.start()
            binding.apply {
                sbProgress.progress = 0
                sbProgress.max = mediaPlayer.duration
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun playNextSong() {
        if (MediaPlayerHelper.currentIndex == MediaPlayerHelper.songsList.lastIndex) {
            MediaPlayerHelper.currentIndex = 0
        } else {
            MediaPlayerHelper.currentIndex += 1
        }
        mediaPlayer.reset()
        setResourcesWithMusic()
    }

    private fun playPreviousSong() {
        if (MediaPlayerHelper.currentIndex == 0) {
            MediaPlayerHelper.currentIndex = MediaPlayerHelper.songsList.lastIndex
        } else {
            MediaPlayerHelper.currentIndex -= 1
        }
        mediaPlayer.reset()
        setResourcesWithMusic()
    }

    private fun pausePlay() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            binding.ivPausePlay.setImageResource(R.drawable.ic_play)
        } else {
            mediaPlayer.start()
            binding.ivPausePlay.setImageResource(R.drawable.ic_pause)

        }
    }

    private fun convertMilliToMMSS(duration: String): String {
        val mm = duration.toLong() / 1000 / 60
        val ss = duration.toLong() / 1000 % 60
        return "${mm.toString().padStart(2, '0')}:" +
                "${ss.toString().padStart(2, '0')}"
    }
}
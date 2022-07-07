package com.example.musicplayer.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.musicplayer.core.MediaPlayerHelper
import com.example.musicplayer.databinding.SongsPlayerFragmentBinding
import java.io.IOException

class SongsPlayerFragment : Fragment() {
    private lateinit var binding: SongsPlayerFragmentBinding

    private val args: SongsPlayerFragmentArgs by navArgs()
    private val mediaPlayer = MediaPlayerHelper.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = SongsPlayerFragmentBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setResourcesWithMusic()
        playMusic()
    }

    private fun setResourcesWithMusic() {
        binding.apply {
            tvPlayerTitle.text = args.song.title
            tvTotalTime.text = convertMilliToMMSS(args.song.duration)
        }
    }

    private fun playMusic(){
        mediaPlayer.reset()
        try {
            mediaPlayer.setDataSource(args.song.path)
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

    private fun convertMilliToMMSS(duration: String): String {
        val mm = duration.toLong() / 1000 / 60
        val ss = duration.toLong() / 1000 % 60
        return "${mm.toString().padStart(2, '0')}:" +
                "${ss.toString().padStart(2, '0')}"
    }



}
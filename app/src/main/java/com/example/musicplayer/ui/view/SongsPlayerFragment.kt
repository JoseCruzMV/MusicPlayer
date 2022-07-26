package com.example.musicplayer.ui.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.musicplayer.R
import com.example.musicplayer.databinding.SongsPlayerFragmentBinding
import com.example.musicplayer.ui.viewmodel.SongsPlayerViewModel

class SongsPlayerFragment : Fragment() {
    private lateinit var binding: SongsPlayerFragmentBinding

    private val songsPlayerViewModel: SongsPlayerViewModel by viewModels ()

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

            ivNext.setOnClickListener { songsPlayerViewModel.nextSong() }
            ivPrevious.setOnClickListener { songsPlayerViewModel.previousSong() }
            ivPausePlay.setOnClickListener { songsPlayerViewModel.pausePlay() }

            sbProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser)
                        songsPlayerViewModel.seekSongTo(progress)
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })
        }
        songsPlayerViewModel.getCurrentSong()

        songsPlayerViewModel.currentSong.observe(viewLifecycleOwner) { currentSong ->
            binding.apply {
                tvPlayerTitle.text = currentSong.title
                sbProgress.progress = 0
                sbProgress.max = currentSong.duration.toInt()
                tvTotalTime.text = convertMilliToMMSS(currentSong.duration)
                ivPausePlay.setImageResource(R.drawable.ic_pause)
            }
        }

        songsPlayerViewModel.isSongPlaying.observe(viewLifecycleOwner) { isSongPlaying ->
            binding.apply {
                if (isSongPlaying)
                    ivPausePlay.setImageResource(R.drawable.ic_pause)
                else
                    ivPausePlay.setImageResource(R.drawable.ic_play)
            }
        }

        /*TODO Implement handler directly in view model */
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    binding.apply {
                        sbProgress.progress = songsPlayerViewModel.getSongProgression()
                        tvCurrentTime.text = convertMilliToMMSS(songsPlayerViewModel.getSongProgression().toString())

                    }
                    handler.postDelayed(this, 1000)
                } catch (e: Exception) {
                    binding.sbProgress.progress = 0
                }
            }
        }, 0)

    }

    private fun convertMilliToMMSS(duration: String): String {
        val mm = duration.toLong() / 1000 / 60
        val ss = duration.toLong() / 1000 % 60
        return "${mm.toString().padStart(2, '0')}:" +
                ss.toString().padStart(2, '0')
    }
}
package com.example.musicplayer.ui.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RemoteViews
import android.widget.SeekBar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.RequestManager
import com.example.musicplayer.R
import com.example.musicplayer.core.CreateNotification
import com.example.musicplayer.databinding.SongsPlayerFragmentBinding
import com.example.musicplayer.ui.viewmodel.SongsPlayerViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SongsPlayerFragment : Fragment() {
    private lateinit var binding: SongsPlayerFragmentBinding

    private val songsPlayerViewModel: SongsPlayerViewModel by activityViewModels()

    @Inject
    lateinit var glideHelper: RequestManager

    private lateinit var customNotification: NotificationCompat.Builder

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
                tvPlayerTitle.isSelected = true
                tvPlayerArtist.text = currentSong.artist
                sbProgress.progress = 0
                sbProgress.max = currentSong.duration.toInt()
                tvTotalTime.text = convertMilliToMMSS(currentSong.duration)
                ivPausePlay.setImageResource(R.drawable.ic_pause)

                val albumUri = Uri.parse("content://media/external/audio/albumart")
                val uri =
                    currentSong.cover?.let { ContentUris.withAppendedId(albumUri, it.toLong()) }

                glideHelper.load(uri)
                    .error(R.drawable.unknown_song)
                    .centerCrop()
                    .into(ivMusicIcon)


                // Notification
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        CreateNotification.CHANNEL_ID,
                        getString(R.string.channel_name),
                        NotificationManager.IMPORTANCE_LOW
                    ).apply {
                        description = getString(R.string.channel_description)
                    }

                    val notificationManager: NotificationManager =
                        activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.createNotificationChannel(channel)
                }
                context?.let {
                    CreateNotification.createNotification(context = it, song = currentSong)
                }
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

        songsPlayerViewModel.songProgress.observe(viewLifecycleOwner) { currentSongPosition ->
            binding.apply {
                sbProgress.progress = currentSongPosition
                tvCurrentTime.text = convertMilliToMMSS(currentSongPosition.toString())
            }
        }
    }

    private fun convertMilliToMMSS(duration: String): String {
        val mm = duration.toLong() / 1000 / 60
        val ss = duration.toLong() / 1000 % 60
        return "${mm.toString().padStart(2, '0')}:" +
                ss.toString().padStart(2, '0')
    }
}
package com.example.musicplayer.ui.view.rostersongs

import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.R
import com.example.musicplayer.databinding.RosterSongsFragmentBinding
import com.example.musicplayer.ui.viewmodel.RosterSongsViewModel
import com.example.musicplayer.ui.viewmodel.SongsPlayerViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RosterSongsFragment : Fragment() {
    private lateinit var binding: RosterSongsFragmentBinding

    private val rosterSongsViewModel: RosterSongsViewModel by viewModels()
    private val songsPlayerViewModel: SongsPlayerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = RosterSongsFragmentBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = RosterSongsAdapter(
            inflater = layoutInflater,
            onClick = ::toSongsPlayerFragment
        )

        binding.songsRecyclerView.apply {
            setAdapter(adapter)
            layoutManager = LinearLayoutManager(context)

            addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
        if (grantPermission()) {
            rosterSongsViewModel.getSongsList()
            rosterSongsViewModel.songsList.observe(viewLifecycleOwner) { currentSongsList ->
                if (currentSongsList.isEmpty()){
                    binding.tvNoSongs.isVisible = true
                } else {
                    adapter.submitList(currentSongsList)
                }
            }
        }

        // Controls
        if (songsPlayerViewModel.currentSong.value != null)
            binding.layoutControls.visibility = View.VISIBLE

        binding.apply {
            ivControlsRosterPausePlay.setOnClickListener { songsPlayerViewModel.pausePlay() }

            sbControlsRosterProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) songsPlayerViewModel.seekSongTo(progress)
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })
        }

        songsPlayerViewModel.isSongPlaying.observe(viewLifecycleOwner) { isSongPlaying ->
            binding.apply {
                if (isSongPlaying)
                    ivControlsRosterPausePlay.setImageResource(R.drawable.ic_pause)
                else
                    ivControlsRosterPausePlay.setImageResource(R.drawable.ic_play)
            }
        }

        songsPlayerViewModel.songProgress.observe(viewLifecycleOwner) { currentSongPosition ->
            binding.apply {
                sbControlsRosterProgress.progress = currentSongPosition
            }
        }
        songsPlayerViewModel.currentSong.observe(viewLifecycleOwner) { currentSong ->
            binding.apply {
                tvControlsRosterSongTitle.text = currentSong.title
                tvControlsRosterSongTitle.isSelected = true
                tvControlsRosterSongArtist.text = currentSong.artist
                tvControlsRosterSongArtist.isSelected = true
                sbControlsRosterProgress.progress = 0
                sbControlsRosterProgress.max = currentSong.duration.toInt()
                ivControlsRosterPausePlay.setImageResource(R.drawable.ic_pause)

                /* TODO make roster images load faster */
                val albumUri = Uri.parse("content://media/external/audio/albumart")
                val uri = currentSong.cover?.let { ContentUris.withAppendedId(albumUri, it.toLong()) }
                Picasso.get().load(uri)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.unknownsong)
                    .into(ivControlsRosterCover)
            }
        }


    }

    private fun grantPermission(): Boolean {
        var result = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            result = when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    true
                }
                shouldShowRequestPermissionRationale(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) -> {
                    toPermissionDeniedFragment()
                    false
                }
                else -> {
                    registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                        if (!isGranted)
                            toPermissionDeniedFragment()
                    }.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    false
                }
            }
        }
        return result
    }

    private fun toSongsPlayerFragment() = findNavController().navigate(
        RosterSongsFragmentDirections.actionRosterSongsFragmentToSongsPlayerFragment()
    )

    private fun toPermissionDeniedFragment() = findNavController().navigate(
        RosterSongsFragmentDirections.actionRosterSongsFragmentToPermissionsDeniedFragment()
    )
}
package com.example.musicplayer.ui.view.rostersongs

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.core.MediaPlayerHelper
import com.example.musicplayer.databinding.RosterSongsFragmentBinding
import com.example.musicplayer.domain.model.AudioModel
import java.io.File

class RosterSongsFragment : Fragment() {
    private lateinit var binding: RosterSongsFragmentBinding

    private val songsList = mutableListOf<AudioModel>()

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
            getSongsList()

            if (songsList.isEmpty()) {
                binding.tvNoSongs.isVisible = true
            } else {
                adapter.submitList(songsList)
                MediaPlayerHelper.songsList = songsList
            }
        }
    }

    private fun getSongsList() {
        if (songsList.isNotEmpty()) songsList.clear()

        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION
        )
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        val cursor = context?.contentResolver?.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )
        cursor?.let {
            while (it.moveToNext()) {
                val songData = AudioModel(
                    title = it.getString(0),
                    path = it.getString(1),
                    duration = it.getString(2)
                )
                if (File(songData.path).exists())
                    songsList.add(songData)
            }
        }
        cursor?.close()
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

    private fun toSongsPlayerFragment(song: AudioModel) = findNavController().navigate(
        RosterSongsFragmentDirections
            .actionRosterSongsFragmentToSongsPlayerFragment(song)
    )

    private fun toPermissionDeniedFragment() = findNavController().navigate(
        RosterSongsFragmentDirections.actionRosterSongsFragmentToPermissionsDeniedFragment()
    )
}
package com.example.musicplayer.ui.view.rostersongs

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.databinding.RosterSongsFragmentBinding
import com.example.musicplayer.domain.model.AudioModel
import com.example.musicplayer.ui.viewmodel.RosterSongsViewModel

class RosterSongsFragment : Fragment() {
    private lateinit var binding: RosterSongsFragmentBinding

    private val rosterSongsViewModel: RosterSongsViewModel by viewModels()

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
                if (currentSongsList.isEmpty()) {
                    binding.tvNoSongs.isVisible = true
                } else {
                    adapter.submitList(currentSongsList)
                }
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
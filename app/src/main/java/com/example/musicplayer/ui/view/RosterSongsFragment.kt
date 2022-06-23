package com.example.musicplayer.ui.view

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.musicplayer.databinding.RosterSongsFragmentBinding

class RosterSongsFragment : Fragment() {
    private lateinit var binding: RosterSongsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = RosterSongsFragmentBinding.inflate(inflater, container, false)
        .apply { binding = this }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        grantPermission()
    }

    private fun grantPermission(): Boolean {
        var result: Boolean = true
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
                        if(!isGranted)
                            toPermissionDeniedFragment()
                    }.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    false
                }
            }
        }
        return result
    }

    private fun toPermissionDeniedFragment() = findNavController().navigate(
        RosterSongsFragmentDirections
            .actionRosterSongsFragmentToPermissionsDeniedFragment()
    )

}
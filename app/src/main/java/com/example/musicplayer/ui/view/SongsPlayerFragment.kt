package com.example.musicplayer.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.musicplayer.databinding.SongsPlayerFragmentBinding

class SongsPlayerFragment : Fragment() {
    private lateinit var binding: SongsPlayerFragmentBinding

    private val args: SongsPlayerFragmentArgs by navArgs()

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
            tvTitle.text = args.song.duration
        }
    }
}
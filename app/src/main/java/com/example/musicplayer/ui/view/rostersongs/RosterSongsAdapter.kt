package com.example.musicplayer.ui.view.rostersongs

import android.content.ContentUris
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.R
import com.example.musicplayer.core.QueueHelper
import com.example.musicplayer.databinding.RosterSongsItemBinding
import com.example.musicplayer.domain.model.AudioModel
import com.squareup.picasso.Picasso

class RosterSongsAdapter(
    private val inflater: LayoutInflater,
    private val onClick: () -> Unit,
) : ListAdapter<AudioModel, RosterRowHolder>(DiffCallBack) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = RosterRowHolder(
        RosterSongsItemBinding.inflate(inflater, parent, false),
        onClick,
    )

    override fun onBindViewHolder(holder: RosterRowHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class RosterRowHolder(
    private val binding: RosterSongsItemBinding,
    private val onClick: () -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(song: AudioModel) = binding.apply {
        val color = ContextCompat.getColor(binding.tvSongTitle.context, R.color.secondary)
        if (layoutPosition == QueueHelper.currentIndex)
            tvSongTitle.setTextColor(color)
        tvSongTitle.text = song.title
        tvSongArtist.text = song.artist

        val albumUri = Uri.parse("content://media/external/audio/albumart")
        val uri = song.cover?.let { ContentUris.withAppendedId(albumUri, it.toLong()) }
        Picasso.get().load(uri)
            .fit()
            .centerCrop()
            .error(R.drawable.unknownsong)
            .into(ivSongCover)

        clItemContainer.setOnClickListener {
            QueueHelper.currentIndex = layoutPosition
            onClick()
        }
    }
}

private object DiffCallBack : DiffUtil.ItemCallback<AudioModel>() {
    override fun areItemsTheSame(oldItem: AudioModel, newItem: AudioModel) =
        oldItem.path == newItem.path

    override fun areContentsTheSame(oldItem: AudioModel, newItem: AudioModel) =
        oldItem.title == newItem.title
}
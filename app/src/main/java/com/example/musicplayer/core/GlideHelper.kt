package com.example.musicplayer.core

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.musicplayer.R

object GlideHelper {

    fun provideGlide(context: Context, uri: Uri?, view: ImageView) {
        Glide.with(context)
            .load(uri)
            .centerCrop()
            .error(R.drawable.unknownsong)
            .into(view)
    }
}
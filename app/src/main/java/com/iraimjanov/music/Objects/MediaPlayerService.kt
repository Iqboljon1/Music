package com.iraimjanov.music.Objects

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri

object MediaPlayerService {
    var mediaPlayer: MediaPlayer? = null
    fun play(context: Context , musicPath: String) {
        mediaPlayer = MediaPlayer.create(context , Uri.parse(musicPath))
    }
}
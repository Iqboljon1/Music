package com.iraimjanov.music.Objects

import android.annotation.SuppressLint
import android.content.Context
import com.iraimjanov.music.models.Music

@SuppressLint("StaticFieldLeak")
object Object {
    var arrayListMusic: ArrayList<Music> = ArrayList()
    var position = 0
    var firstTime = true
    var playFragment = false
    var homeFragment = false
}
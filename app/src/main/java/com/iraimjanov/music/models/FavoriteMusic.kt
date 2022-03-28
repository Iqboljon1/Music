package com.iraimjanov.music.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class FavoriteMusic {
    @PrimaryKey(autoGenerate = false)
    var id: Long? = null
    var title: String? = null
    var imagePath: String? = null
    var musicPath: String? = null
    var author: String? = null

    constructor(
        id: Long?,
        title: String?,
        imagePath: String?,
        musicPath: String?,
        author: String?,
    ) {
        this.id = id
        this.title = title
        this.imagePath = imagePath
        this.musicPath = musicPath
        this.author = author
    }

    constructor()

}
package com.iraimjanov.music.models

import androidx.room.*

@Dao
interface MyDao {
    @Transaction
    @Query("select * from FavoriteMusic")
    fun show(): List<FavoriteMusic>

    @Insert
    fun addMusic(favoriteMusic: FavoriteMusic)

    @Delete
    fun deleteMusic(favoriteMusic: FavoriteMusic)

}
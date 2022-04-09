package com.example.giniappsflow.database.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface GalleryDataBaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg images: Image)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(image: Image)

    @Query("SELECT * FROM Image")
    fun getGallery(): Flow<Image>
}
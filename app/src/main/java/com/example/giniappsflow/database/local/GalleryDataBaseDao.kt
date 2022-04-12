package com.example.giniappsflow.database.local

import androidx.room.*
import com.example.giniappsflow.model.Image
import kotlinx.coroutines.flow.Flow


@Dao
interface GalleryDataBaseDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(images: List<DatabaseImage>)

    @Query("UPDATE DatabaseImage SET success = :success  WHERE uri = :uri")
    fun updateSuccess(success: Boolean,uri: String)

    @Query("UPDATE DatabaseImage SET link = :link ,success = :success WHERE uri = :uri")
    fun updateLink(link: String,uri: String,success: Boolean)

    @Query("SELECT * FROM DatabaseImage")
    fun getGallery(): Flow<List<Image>>

    @Query("SELECT * FROM DatabaseImage WHERE success = 1")
    fun getLinks():Flow<List<Image>>
}

package com.example.giniappsflow.repository

import com.example.giniappsflow.database.local.Image
import com.example.giniappsflow.database.local.ImageDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext

class ImageRepository (private val database: ImageDataBase) {

    suspend fun pushAllGallery(gallery:SharedFlow<Image>) {
        withContext(Dispatchers.IO) {
            gallery.collect{
                database.galleryDataBaseDao.insert(it)
            }
        }
    }

    suspend fun getGallery():Flow<Image> {
        lateinit var result:Flow<Image>
        withContext(Dispatchers.IO){
            result = database.galleryDataBaseDao.getGallery()
        }
        return result
    }
}

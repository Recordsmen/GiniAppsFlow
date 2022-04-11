package com.example.giniappsflow.repository

import com.example.giniappsflow.NetworkUtils.asDomainModel
import com.example.giniappsflow.database.local.ImageDataBase
import com.example.giniappsflow.model.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ImageRepository (private val database: ImageDataBase) {

    suspend fun pushAllGallery(gallery: List<Image>) {
        withContext(Dispatchers.IO) {
            database.galleryDataBaseDao.insertAll(gallery.asDomainModel())
        }
    }

    suspend fun getGallery(): Flow<List<Image>> {
        lateinit var result: Flow<List<Image>>
        withContext(Dispatchers.IO){
            result = database.galleryDataBaseDao.getGallery()
        }
        return result
    }

    suspend fun changeStatus(image: String){
        withContext(Dispatchers.IO){
            database.galleryDataBaseDao.insert(image)
        }
    }

    suspend fun getSendedImages(): Flow<List<Image>>{
        lateinit var result: Flow<List<Image>>
        withContext(Dispatchers.IO){
            result = database.galleryDataBaseDao.getAllSentImages()
        }
        return result
    }

    suspend fun changeSuccess(uri: String){
        withContext(Dispatchers.IO){
            database.galleryDataBaseDao.updateSuccess(true,uri)
        }
    }

    suspend fun changeLink(uri: String,link:String,success:Boolean) {
        withContext(Dispatchers.IO) {
            database.galleryDataBaseDao.updateLink(link, uri, success)
        }
    }

    suspend fun getLinks(): Flow<List<Image>>{
        lateinit var result: Flow<List<Image>>
        withContext(Dispatchers.IO){
            result = database.galleryDataBaseDao.getLinks()
        }
        return result
    }
}

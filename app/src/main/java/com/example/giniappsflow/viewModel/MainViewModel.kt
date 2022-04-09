package com.example.giniappsflow.viewModel

import android.app.Application
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.giniappsflow.database.local.Image
import com.example.giniappsflow.database.local.ImageDataBase
import com.example.giniappsflow.repository.ImageRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var startingRow = 0
    private var allLoaded = false
    lateinit var imageUriList: SharedFlow<String>
    lateinit var loadingImages: Flow<String>
    lateinit var images:Flow<Image>
    val newGal: MutableSharedFlow<Image> = MutableSharedFlow(1000)


    private val database = ImageDataBase.getDatabase(application.applicationContext)
    private val imageRepository = ImageRepository(database)

    fun getImagesFromGallery(context: Context) {
        viewModelScope.launch {
            fetchGalleryImages(context)
            gettingImages()
            pushGalleryIntoDatabase()
            getImagesFromRepository()
        }
    }

    fun getImagesFromRepository(){
        viewModelScope.launch {
            images = imageRepository.getGallery()
        }
    }
    private suspend fun gettingImages(){
        imageUriList.collect{
            newGal.tryEmit(Image(it))
        }
    }
    private suspend fun pushGalleryIntoDatabase(){
        imageRepository.pushAllGallery(newGal.asSharedFlow())
    }

    private fun fetchGalleryImages(context: Context){
        val columns = arrayOf(
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media._ID,
        )
        val orderBy = MediaStore.Images.Media.DATE_ADDED
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
            null, "$orderBy DESC"
        )
        Log.i("GalleryAllLoaded", "$allLoaded")

        if (cursor != null && !allLoaded) {
            val totalRows = cursor.count
            val mutableimageUriList:MutableSharedFlow<String> = MutableSharedFlow(
                replay = totalRows,
                extraBufferCapacity = 10,
                onBufferOverflow = BufferOverflow.SUSPEND
            )
            getImages(totalRows,cursor, mutableimageUriList)
            cursor.close()

        }
    }


    private fun getImages(totalRows:Int, cursor:Cursor,mutableImageFlow: MutableSharedFlow<String>){
        for (i in startingRow until totalRows){
            cursor.moveToPosition(i)
            val dataIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            mutableImageFlow.tryEmit(cursor.getString(dataIndex))
        }
        imageUriList = mutableImageFlow.asSharedFlow()
    }
    fun loadImage(path:String){
        val listOfLoadImage = mutableListOf<String>()
        listOfLoadImage.add(path)
        loadingImages = listOfLoadImage.asFlow()

    }


}
package com.example.giniappsflow.viewModel

import android.app.Application
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.giniappsflow.NetworkUtils.*
import com.example.giniappsflow.model.Image
import com.example.giniappsflow.database.local.ImageDataBase
import com.example.giniappsflow.repository.ImageRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.json.JSONObject
import org.json.JSONTokener
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.lang.Exception
import java.net.URL
import javax.net.ssl.HttpsURLConnection

const val TAG = "MAIN_VIEW_MODEL"
class MainViewModel(application: Application) : AndroidViewModel(application) {

    var imageUriList: MutableList<String> = mutableListOf()
    private var listOfImages: MutableList<Image> = mutableListOf()
    val list = mutableListOf<String>()

    private val _image = MutableSharedFlow<List<Image>>()
    val images:SharedFlow<List<Image>> = _image

    private val _links = MutableSharedFlow<List<Image>>()
    val links:SharedFlow<List<Image>> = _links

    private val _error = MutableSharedFlow<String>()
    val error:SharedFlow<String> = _error

    private val database = ImageDataBase.getDatabase(application.applicationContext)
    private val imageRepository = ImageRepository(database)

    fun getImagesFromGallery(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                fetchGalleryImages(context, imageUriList)
                gettingImages()
                pushGalleryIntoDatabase()
                getImagesFromRepository()
            }
        }
    }

    private suspend fun getImagesFromRepository(){
        imageRepository.getGallery().collect{
            _image.emit(it)
        }
    }

    private fun gettingImages(){
        imageUriList.forEach {
            listOfImages.add(Image(it))
        }
    }

    private suspend fun pushGalleryIntoDatabase(){
        imageRepository.pushAllGallery(listOfImages)
    }

    private fun fetchGalleryImages(context: Context,imageUriList:MutableList<String>){
        val columns = arrayOf(
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media._ID,
        )
        val orderBy = MediaStore.Images.Media.DATE_ADDED
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
            null, "$orderBy DESC"
        )
        Log.i("GalleryAllLoaded", "False")

        if (cursor != null) {
            val totalRows = cursor.count

            getImages(totalRows,cursor, imageUriList)
            cursor.close()
        }
    }

    private fun getImages(totalRows:Int, cursor:Cursor,ImageUriList: MutableList<String>){
        for (i in 0 until totalRows){
            cursor.moveToPosition(i)
            val dataIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            ImageUriList.add(cursor.getString(dataIndex))
        }
    }

    fun uploadImageToImgur(image: Bitmap,path: String) {
            getBase64Image(image, complete = { base64Image ->
                viewModelScope.launch(Dispatchers.IO) {
                    val url = URL("https://api.imgur.com/3/image")

                    val boundary = "Boundary-${System.currentTimeMillis()}"
                    try {
                        val httpsURLConnection = url.openConnection() as HttpsURLConnection
                        httpsURLConnection.setRequestProperty("Authorization", Api.KEY)
                        httpsURLConnection.setRequestProperty(
                            "Content-Type",
                            "multipart/form-data; boundary=$boundary"
                        )
                        httpsURLConnection.requestMethod = "POST"
                        httpsURLConnection.doInput = true
                        httpsURLConnection.doOutput = true

                        var body = ""
                        body += "--$boundary\r\n"
                        body += "Content-Disposition:form-data; name=\"image\""
                        body += "\r\n\r\n$base64Image\r\n"
                        body += "--$boundary--\r\n"

                        val outputStreamWriter = OutputStreamWriter(httpsURLConnection.outputStream)
                        outputStreamWriter.write(body)
                        outputStreamWriter.flush()
                        val response = httpsURLConnection.inputStream.bufferedReader()
                            .use { it.readText() }

                        val jsonObject = JSONTokener(response).nextValue() as JSONObject
                        val data = jsonObject.getJSONObject("data")
                        val success = jsonObject.getBoolean("success")

                        Log.d("TAG", "Link is : ${data.getString("link")}")
                        val imgurUrl = data.getString("link")
                        list.add(imgurUrl)
                        changeLink(path, imgurUrl, success)
                    } catch (e: Exception) {
                        _error.emit(e.toString())
                        errorLink(path, false)
                    }
                }
            })
    }
    private fun getBase64Image(image: Bitmap, complete: (String) -> Unit) {
            val outputStream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val b = outputStream.toByteArray()
            complete(Base64.encodeToString(b, Base64.DEFAULT))
    }
    fun changeLink(uri: String, link:String, success:Boolean){
        viewModelScope.launch {
            imageRepository.changeLink(uri,link,success)
        }
    }
    fun errorLink(uri: String,success: Boolean){
        viewModelScope.launch {
            imageRepository.changeError(uri,success)
        }
    }
    fun getAllLinks(){
        viewModelScope.launch {
            imageRepository.getLinks().collect{
                _links.emit(it)
            }
        }
    }
}

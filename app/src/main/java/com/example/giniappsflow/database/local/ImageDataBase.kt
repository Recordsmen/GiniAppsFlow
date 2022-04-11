package com.example.giniappsflow.database.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DatabaseImage::class], version = 5)

abstract class ImageDataBase: RoomDatabase() {

    abstract val galleryDataBaseDao: GalleryDataBaseDao

    companion object{
        @Volatile
        private var INSTANCE:ImageDataBase? = null

        fun getDatabase(context: Context): ImageDataBase{
            synchronized(this){
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ImageDataBase::class.java,
                        "gallery-database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }

}
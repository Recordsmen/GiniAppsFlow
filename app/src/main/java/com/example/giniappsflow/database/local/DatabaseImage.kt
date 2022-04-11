package com.example.giniappsflow.database.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatabaseImage(
    @PrimaryKey
    val uri: String,
    val status: Int = 0,
    val success: Boolean = false,
    val link: String = ""
)


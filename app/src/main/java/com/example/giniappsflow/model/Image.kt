package com.example.giniappsflow.model

data class Image(
    val uri: String,
    val status: Int = 0,
    val success: Boolean = false,
    val link:String = ""
)
package com.example.giniappsflow.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UploadResponse(
    @Json(name = "data")
    val upload: String,
    @Json(name = "status")
    val status: Int = 0,
    @Json(name = "success")
    val success: Boolean = false
)
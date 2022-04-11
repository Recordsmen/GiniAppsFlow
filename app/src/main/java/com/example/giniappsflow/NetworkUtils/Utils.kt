package com.example.giniappsflow.NetworkUtils

import com.example.giniappsflow.database.local.DatabaseImage
import com.example.giniappsflow.model.Image

fun List<Image>.asDomainModel(): List<DatabaseImage> {
    return map {
        DatabaseImage(
            uri = it.uri,
            status = it.status,
            success = it.success,
            link = ""
        )
    }
        .toList()
}
object Api {
    const val KEY ="Client-ID 5445878f49c7b90"
}
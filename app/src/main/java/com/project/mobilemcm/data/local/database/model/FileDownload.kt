package com.project.mobilemcm.data.local.database.model

data class FileDownload(
    val id:String,
    val name:String,
    val type:String,
    val url:String,
    var downloadedUri:String?=null,
    var isDownloading:Boolean = false,
)

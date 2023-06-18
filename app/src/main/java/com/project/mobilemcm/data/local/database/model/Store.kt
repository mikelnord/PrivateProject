package com.project.mobilemcm.data.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Store(
    @PrimaryKey
    val id: String,
    val division_id:String,
    val code: String,
    val name: String,
    val deletionmark:Boolean
)
package com.project.mobilemcm.data.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Division(
    @PrimaryKey
    val id: String,
    val name: String?,
    val code: String?,
    val deletionmark: Boolean?
)
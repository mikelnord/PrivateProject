package com.project.mobilemcm.data.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ObmenDate(
    @PrimaryKey
    val key:Int=0,
    val dateObmen:String
)

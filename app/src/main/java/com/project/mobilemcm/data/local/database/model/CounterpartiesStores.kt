package com.project.mobilemcm.data.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CounterpartiesStores(
    @PrimaryKey
    val id:String,
    val name:String?,
    val company:String?,
    val address:String?,
    val latitude:Double?,
    val longitude:Double?,
    val deletionmark:Boolean?
)
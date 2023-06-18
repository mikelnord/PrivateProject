package com.project.mobilemcm.data.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LoggedInUser(
    @PrimaryKey
    val id:String,
    val division_id: String,
    val displayName: String,
    val login:String,
    val password:String,
    val timeLogin:Long,
    val isActive:Boolean=false,
    val timeWork:Long=0L
)
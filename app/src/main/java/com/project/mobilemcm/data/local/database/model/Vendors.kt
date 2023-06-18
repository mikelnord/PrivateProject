package com.project.mobilemcm.data.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Vendors(
    @PrimaryKey(autoGenerate = true) val vendor_id: Long=0,
    val name: String
)

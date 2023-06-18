package com.project.mobilemcm.data.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Pricegroups2(
    @PrimaryKey
    val id: String,
    val code: String,
    val name: String
)
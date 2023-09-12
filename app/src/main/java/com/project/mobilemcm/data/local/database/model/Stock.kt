package com.project.mobilemcm.data.local.database.model

import androidx.room.Entity

@Entity(primaryKeys = ["good_id","store_id"])
data class Stock(
    val amount: Double,
    val good_id: String,
    val store_id: String,
    val price: Double,
    val reserve:Double
)
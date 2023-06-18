package com.project.mobilemcm.pricing.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(indices = [Index(value = ["id"])])
data class Item(
    @PrimaryKey(autoGenerate = true) val item_id: Long=0,
    val id: String,
    val discount: Double,
    val pricegroup: String?,
    val pricegroup2: String?,
    val good_id: String?
)
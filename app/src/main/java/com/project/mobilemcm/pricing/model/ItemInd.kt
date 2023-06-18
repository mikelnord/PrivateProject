package com.project.mobilemcm.pricing.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["id","good_id"])])
data class ItemInd(
    @PrimaryKey(autoGenerate = true) val item_id: Long=0,
    val id:String,
    val good_id:String,
    val price:Double
)
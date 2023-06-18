package com.project.mobilemcm.data.local.database.model

import androidx.room.*

@Entity(indices = [Index(value=["category"])])
data class Good(
    @PrimaryKey
    val id: String,
    val category: String,
    val code: String,
    val fullname: String,
    val name: String,
    val pricegroup: String,
    val pricegroup2: String,
    val vendor: String,
    val vendorCode: String, //Артикул
    val deletionmark:Boolean
)
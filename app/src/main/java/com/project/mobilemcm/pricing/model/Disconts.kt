package com.project.mobilemcm.pricing.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Disconts(
    @PrimaryKey
    val id: String,
    val active: String,
    val all: String,
    var companies: List<Company>,
    val date: String,
    val date_end: String,
    val date_start:String,
    val number: String,
    var items: List<Item>,
    val type: String
)
package com.project.mobilemcm.pricing.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["id","company_id"])])
data class Company(
    @PrimaryKey(autoGenerate = true) val item_id: Long=0,
    val company_id: String,
    val id: String
)
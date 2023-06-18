package com.project.mobilemcm.pricing.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class IndividualPrices(
    @PrimaryKey
    val id: String,
    val active: String,
    val company_id: String,
    val date: String,
    val date_end: String,
    val date_start: String,
    val number: String,
    var items: List<ItemInd>?,
)
package com.project.mobilemcm.data.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(
    @PrimaryKey
    val id: String,
    val code: String,
    val isfolder: Boolean,
    val name: String,
    val parent_id: String,
    val deletionmark: Boolean
)

data class DomainCategory(
    val id: String,
    val parent_id: String,
    val name: String,
    val code: String,
    val amount: Double?,
    val pricegroup: String,
    val vendor: String
)

data class ParentCategory(
    val parent_id: String,
    val name: String,
    val code: String
)
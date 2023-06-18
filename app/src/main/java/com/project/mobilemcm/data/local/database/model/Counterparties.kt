package com.project.mobilemcm.data.local.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value=["name"])])
data class Counterparties(
    @PrimaryKey
    val id: String,
    val name: String?=null,
    val user: String?=null,
    val deletionmark: Boolean?=null,
    val apply_actions: Boolean?=null,
    val internet:Boolean?=null
)
package com.project.mobilemcm.data.local.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value=["company"])])
data class Contract(
    @PrimaryKey
    val id: String,
    val company: String,
    val name: String?,
    val type: String?,
    val number: String?,
    val deletionmark:Boolean?,
    val maxsumm:Double?,
    val maxdays:Int?,
    val delivery:Int?
)


//"contracts": [
//{
//    "id": "37e8d2e6-c461-11df-8025-001517890160",
//    "company": "37e8d2e2-c461-11df-8025-001517890160",
//    "name": "основной",
//    "type": "Безналичный",
//    "number": "200910-03",
//    "deletionmark": false,
//    "maxsumm": 0,
//    "maxdays": 14,
//    "delivery": 0
//},
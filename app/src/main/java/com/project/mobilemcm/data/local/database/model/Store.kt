package com.project.mobilemcm.data.local.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Store(
    @PrimaryKey
    val id: String,
    val division_id:String,
    val code: String,
    val name: String,
    @ColumnInfo(defaultValue = "0")
    val default:Boolean=false,
    val deletionmark:Boolean
)

//"id": "1a597162-c316-11e6-80fe-001e67921ce8",
//"division_id": "a4786126-c672-11e6-80fe-001e67921ce8",
//"name": "Красноярск, основной склад",
//"code": "000000121",
//"default": true,
//"deletionmark": false
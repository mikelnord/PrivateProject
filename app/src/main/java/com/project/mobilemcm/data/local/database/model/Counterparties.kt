package com.project.mobilemcm.data.local.database.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["name"])])
data class Counterparties(
    @PrimaryKey
    val id: String,
    val name: String? = null,
    val user: String? = null,
    val deletionmark: Boolean? = null,
    val apply_actions: Boolean? = null,
    val internet: Boolean? = null,
    val default_contract: String = "",
    val email: String = ""
)


//"id": "2b2901fa-127d-11e6-80f3-001e67921ce7",
//"name": "Бизон СТО",
//"user": "c083b343-9fbd-11e6-80fc-001e67921ce7",
//"deletionmark": false,
//"apply_actions": true,
//"internet": false,
//"default_contract": "",
//"email": ""
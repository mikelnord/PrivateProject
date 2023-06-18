package com.project.mobilemcm.data.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity
data class RequestDocument(
    @PrimaryKey(autoGenerate = true) val document_id: Long = 0,
    var number: String? = "",
    val docDate: Calendar = Calendar.getInstance(),
    var counterparties_id: String,
    var counterpartiesStores_id: String = "",
    var store_id: String,
    var isPickup: Boolean = false
)

data class RequestDocumentItem(
    val document_id: Long,
    val docDate: Calendar,
    val nameStore: String,
    val nameCounterparties: String
)

data class RequestDocument1c(
    val id_doc: Long,
    val docDate: String,
    val counterparties_id: String,
    val counterpartiesStores_id: String,
    val store_id: String,
    val isPickup: Boolean,
    val userId: String,
    val itemList: List<Good1c>
)
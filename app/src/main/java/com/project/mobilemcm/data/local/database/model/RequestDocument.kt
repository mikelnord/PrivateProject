package com.project.mobilemcm.data.local.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Entity
data class RequestDocument(
    @PrimaryKey(autoGenerate = true) val document_id: Long = 0,
    var number: String? = "",
    val docDate: Calendar = Calendar.getInstance(),
    var counterparties_id: String,
    var counterpartiesStores_id: String = "",
    var store_id: String,
    var isPickup: Boolean = false,
    var comment: String = "",
    var isSent: Boolean = false,
    var idOneC: String = "",
    var summDoc: Double = 0.0
)

data class RequestDocumentItem(
    val document_id: Long,
    val docDate: Calendar,
    val nameStore: String,
    val nameCounterparties: String,
    var isSent: Boolean,
    val summDoc: Double,
    val number: String?
)

data class RequestDocument1c(
    val id_doc: Long,
    val docDate: String,
    val counterparties_id: String,
    val counterpartiesStores_id: String,
    val store_id: String,
    val isPickup: Boolean,
    val userId: String,
    val comment: String,
    val itemList: List<Good1c>
)

fun RequestDocument.RequestDocumentToRequestDocument1c(
    userId: String,
    itemList: List<Good1c>
) =
    RequestDocument1c(
        userId = userId,
        id_doc = document_id,
        docDate = LocalDateTime.ofInstant(
            docDate.toInstant(),
            docDate.timeZone.toZoneId()
        ).format(DateTimeFormatter.ISO_DATE_TIME),
        store_id = store_id,
        counterparties_id = counterparties_id,
        counterpartiesStores_id = counterpartiesStores_id,
        isPickup = isPickup,
        itemList = itemList,
        comment = comment
    )
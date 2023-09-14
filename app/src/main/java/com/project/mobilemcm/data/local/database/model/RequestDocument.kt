package com.project.mobilemcm.data.local.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.UUID

@Entity
data class RequestDocument(
    @PrimaryKey(autoGenerate = true) val document_id: Long = 0,
    var number: String? = "",
    val docDate: Calendar = Calendar.getInstance(),
    var counterparties_id: String,
    var counterpartiesStores_id: String = "",
    var store_id: String,
    var store_name: String = "",
    var isPickup: Boolean = false,
    var comment: String = "",
    var isSent: Boolean = false,
    val idOneC: String = UUID.randomUUID().toString(),
    var summDoc: Double = 0.0,
    var markToDelete: Boolean = false,
    @ColumnInfo(defaultValue = "")
    var contract_id: String = "",
    var contract_type:String=""
)

data class RequestDocumentItem(
    val document_id: Long,
    val idOneC: String,
    val docDate: Calendar,
    val nameStore: String,
    val nameCounterparties: String,
    var isSent: Boolean,
    val summDoc: Double,
    val number: String?,
    val email:String
)

data class RequestDocument1c(
    val version: Int = 1,
    val idOneC: String,
    val id_doc: Long,
    val docDate: String,
    val counterparties_id: String,
    val counterpartiesStores_id: String,
    val contract_id: String,
    val store_id: String,
    val isPickup: Boolean,
    val userId: String,
    val comment: String,
    val markToDelete: Boolean = false,
    val itemList: List<Good1c>
)

fun RequestDocument.RequestDocumentToRequestDocument1c(
    userId: String,
    itemList: List<Good1c>
) =
    RequestDocument1c(
        userId = userId,
        id_doc = document_id,
        idOneC = idOneC,
        docDate = LocalDateTime.ofInstant(
            docDate.toInstant(),
            docDate.timeZone.toZoneId()
        ).format(DateTimeFormatter.ISO_DATE_TIME),
        store_id = store_id,
        counterparties_id = counterparties_id,
        counterpartiesStores_id = counterpartiesStores_id,
        contract_id = contract_id,
        isPickup = isPickup,
        itemList = itemList,
        comment = comment
    )
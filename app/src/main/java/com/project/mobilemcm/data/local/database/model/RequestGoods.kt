package com.project.mobilemcm.data.local.database.model

import androidx.room.Entity


@Entity(primaryKeys = ["idDoc", "idGood"])
data class RequestGoods(
    val idDoc: Long,
    val idGood: String,
    val count: Double,
    val price: Double,
    var number: String?,
    var priceInd: Double?,
    var discont: Double?,
    var metod:Int?
)

package com.project.mobilemcm.data.local.database.model


data class GoodWithStock(
    val id: String,
    val name: String?,
    val vendorCode: String?,
    var amount: Double?,
    val price: Double?,
    val pricegroup: String,
    val pricegroup2: String,
    val category: String,
    val vendors: String,
    val namePricegroup: String = "",
    var count: Double,
    var number: String?,
    var priceInd: Double?,
    var discont: Double?,
    var reserve:Double=0.0,
    var metod:Int?=0
)

data class Good1c(
    val id: String,
    val price: Double?,
    var count: Double,
    val discount: Double,
    val metod: Int?
)
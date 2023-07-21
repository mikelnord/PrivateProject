package com.project.mobilemcm.pricing.logic

data class IndPrices(
    val number: String,
    val price: Double,
    val discount: Double,
    val metod:Int
)

data class DiscountCompany(
    val date:String,
    val name:String,
    val discount: Double
)
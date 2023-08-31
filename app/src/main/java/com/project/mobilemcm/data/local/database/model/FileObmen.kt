package com.project.mobilemcm.data.local.database.model

import com.project.mobilemcm.pricing.model.ActionPrices
import com.project.mobilemcm.pricing.model.Disconts
import com.project.mobilemcm.pricing.model.IndividualPrices

data class FileObmen(
    val version:String,
    val categories: List<Category>?,
    val date: String,
    val divisions:List<Division>?,
    val goods: List<Good>?,
    val pricegroups: List<Pricegroup>?,
    val pricegroups2: List<Pricegroups2>?,
    val stocks: List<Stock>?,
    val store: List<Store>?,
    val companies : List<Counterparties>?,
    val company_stores: List<CounterpartiesStores>?,
    val discounts:List<Disconts>?,
    val action_prices:List<ActionPrices>?,
    val individual_prices:List<IndividualPrices>?
)

data class FileUsers(
    val users: List<User>
)
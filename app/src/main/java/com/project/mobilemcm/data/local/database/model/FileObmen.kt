package com.project.mobilemcm.data.local.database.model

import com.project.mobilemcm.pricing.model.ActionPrices
import com.project.mobilemcm.pricing.model.Disconts
import com.project.mobilemcm.pricing.model.IndividualPrices

data class FileObmen(
    val version: String,
    val categories: List<Category>?,
    val date: String,
    val divisions: List<Division>?,
    val goods: List<Good>?,
    val pricegroups: List<Pricegroup>?,
    val pricegroups2: List<Pricegroups2>?,
    val stocks: List<Stock>?,
    val store: List<Store>?,
    val companies: List<Counterparties>?,
    val company_stores: List<CounterpartiesStores>?,
    val discounts: List<Disconts>?,
    val action_prices: List<ActionPrices>?,
    val individual_prices: List<IndividualPrices>?,
    val contracts: List<Contract>?
)

data class FileUsers(
    val users: List<User>
)

data class Debets(
    val debets: List<DebetItem>
)

data class DebetItem(
    val client: String,
    val contract: String,
    val debt: String,
    val overdue_debt: String,
    val overdue_debt5: String,
    val delivery: String,
    val days: String,
    val limit: String
)

data class Payments(
    val payments: List<PaymentsItem>
)

data class PaymentsItem(
    val client: String,
    val contract: String,
    val sum: String,
    val date: String,
    val number: String,
    val purpose: String
)

//"debets": [
//{
//    "client": "Примстройактив  ООО",
//    "contract": "основной",
//    "debt": 62155.2, сортировка в порядке убыанвания2 общая сумма
//    "overdue_debt": 62155.2, сортировка в порядке убыанвания1 красным>0 просрочка
//    "overdue_debt5": 0,
//    "delivery": 3,
//    "days": 14,
//    "limit": 150000
//},

//"payments": [
//{
//    "client": "Петрова Алина Артемовна ИП",
//    "contract": "Отсрочка 14 дней",
//    "sum": 42673.5,
//    "date": "2023-09-11T00:00:00",
//    "number": "317",
//    "purpose": "ОПЛАТА ПО СЧЕТУ № МСГМ0007211 ОТ 04 СЕНТЯБРЯ 2023. В ТОМ ЧИСЛЕ НДС 20%, 7112.25 РУБ."
//},
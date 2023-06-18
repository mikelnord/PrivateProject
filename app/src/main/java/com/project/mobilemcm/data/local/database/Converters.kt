package com.project.mobilemcm.data.local.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.mobilemcm.pricing.model.Company
import com.project.mobilemcm.pricing.model.Item
import com.project.mobilemcm.pricing.model.ItemAction
import com.project.mobilemcm.pricing.model.ItemInd
import java.util.Calendar

class Converters {
    @TypeConverter
    fun calendarToDatestamp(calendar: Calendar): Long = calendar.timeInMillis

    @TypeConverter
    fun datestampToCalendar(value: Long): Calendar =
        Calendar.getInstance().apply { timeInMillis = value }


    private inline fun <reified T> Gson.fromJson(json: String): T =
        fromJson<T>(json, object : TypeToken<T>() {}.type)

    @TypeConverter
    fun fromStringCompanyList(value: List<Company>): String {

        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringCompanyList(value: String): List<Company> {
        return try {
            Gson().fromJson<List<Company>>(value)
        } catch (e: Exception) {
            arrayListOf()
        }
    }

    @TypeConverter
    fun fromItemToJSON(itemList: List<Item>): String {
        return Gson().toJson(itemList)
    }

    @TypeConverter
    fun fromJSONToItem(json: String): List<Item> {
        return try {
            Gson().fromJson<List<Item>>(json)
        } catch (e: Exception) {
            arrayListOf()
        }
    }

    @TypeConverter
    fun fromItemActionToJSON(itemList: List<ItemAction>?): String? {
        return itemList?.let { Gson().toJson(itemList) }
    }

    @TypeConverter
    fun fromJSONToItemAction(json: String?): List<ItemAction>? {
        return json?.let { Gson().fromJson(json) }
    }

    @TypeConverter
    fun fromItemIndToJSON(itemList: List<ItemInd>?): String {
        return try {
            Gson().toJson(itemList)
        } catch (e: Exception) {
            ""
        }
    }

    @TypeConverter
    fun fromJSONToItemInd(json: String): List<ItemInd> {
        return try {
            arrayListOf()
        } catch (e: Exception) {
            arrayListOf()
        }
    }
}
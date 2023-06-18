package com.project.mobilemcm.pricing.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.mobilemcm.pricing.model.Item


@Dao
interface ItemDao {

    @Query("SELECT * FROM item ")
    suspend fun getAll(): List<Item>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(item: List<Item>)

    @Delete
    suspend fun delete(item: Item)

    @Query("DELETE FROM item")
    suspend fun clearItem()

    @Query("SELECT COUNT(*) FROM Item")
    suspend fun getCountItem(): Int

}
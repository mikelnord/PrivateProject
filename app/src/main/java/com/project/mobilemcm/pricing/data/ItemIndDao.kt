package com.project.mobilemcm.pricing.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.mobilemcm.pricing.model.ItemInd


@Dao
interface ItemIndDao {

    @Query("SELECT * FROM ItemInd ")
    suspend fun getAll(): List<ItemInd>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(itemInd: List<ItemInd>?)

    @Delete
    suspend fun delete(itemInd: ItemInd)

    @Query("DELETE FROM ItemInd")
    suspend fun clearItemInd()

    @Query("SELECT COUNT(*) FROM itemind")
    suspend fun getCountItemInd(): Int

}
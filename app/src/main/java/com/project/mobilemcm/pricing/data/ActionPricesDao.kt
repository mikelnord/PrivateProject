package com.project.mobilemcm.pricing.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.mobilemcm.pricing.model.ActionPrices


@Dao
interface ActionPricesDao {

    @Query("SELECT * FROM ActionPrices ")
    suspend fun getAll(): List<ActionPrices>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(disconts: List<ActionPrices>)

    @Delete
    suspend fun delete(actionPrices: ActionPrices)

    @Query("DELETE FROM ActionPrices")
    suspend fun clearActionPrices()

    @Query("SELECT COUNT(*) FROM ACTIONPRICES")
    suspend fun getCountActionPrices(): Int

}
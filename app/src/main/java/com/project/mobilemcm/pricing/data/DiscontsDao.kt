package com.project.mobilemcm.pricing.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.mobilemcm.pricing.model.Disconts

@Dao
interface DiscontsDao {

    @Query("SELECT * FROM Disconts ")
    suspend fun getAll(): List<Disconts>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(disconts: List<Disconts>)

    @Delete
    suspend fun delete(disconts: Disconts)

    @Query("DELETE FROM Disconts")
    suspend fun clearDisconts()

    @Query("SELECT COUNT(*) FROM DISCONTS")
    suspend fun getCountDicsonts(): Int

}
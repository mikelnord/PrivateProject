package com.project.mobilemcm.data.local.database

import androidx.room.*
import com.project.mobilemcm.data.local.database.model.Pricegroups2
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface Pricegroups2Dao {

    @Query("SELECT * FROM pricegroups2 order by name")
    fun getAll(): Flow<List<Pricegroups2>>

    fun getAllDistinct()=getAll().distinctUntilChanged()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pricegroups2: List<Pricegroups2>)

    @Delete
    suspend fun delete(pricegroups2: Pricegroups2)

    @Query("DELETE FROM pricegroups2")
    suspend fun clearPricegroups2()

    @Query("SELECT COUNT(*) FROM pricegroups2")
    suspend fun getCountPricegroups2():Int
}
package com.project.mobilemcm.data.local.database

import androidx.room.*
import com.project.mobilemcm.data.local.database.model.Pricegroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface PricegroupDao {

    @Query("SELECT * FROM pricegroup where name LIKE '%' || :name || '%' order by name")
    fun getAll(name: String): Flow<List<Pricegroup>>

    @Query("Select * from pricegroup where id=:id")
    suspend fun getPricegroup(id: String):Pricegroup

    fun getAllDistinct(name: String) = getAll(name).distinctUntilChanged()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pricegroup: List<Pricegroup>)

    @Delete
    suspend fun delete(pricegroup: Pricegroup)

    @Query("DELETE FROM pricegroup")
    suspend fun clearPricegroup()

    @Query("SELECT COUNT(*) FROM pricegroup")
    suspend fun getCountPricegroup(): Int

}
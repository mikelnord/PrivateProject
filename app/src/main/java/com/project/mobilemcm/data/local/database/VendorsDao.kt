package com.project.mobilemcm.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.mobilemcm.data.local.database.model.Vendors
import kotlinx.coroutines.flow.Flow

@Dao
interface VendorsDao {
    @Query("SELECT * FROM vendors order by name")
    suspend fun getAll(): List<Vendors>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vendors: List<Vendors>)

    @Delete
    suspend fun delete(vendors: Vendors)

    @Query("DELETE FROM vendors")
    suspend fun clearVendors()

    @Query("SELECT * FROM vendors where name LIKE '%' || :name || '%' order by name")
    fun getVendors(name: String): Flow<List<Vendors>>

    @Query("SELECT COUNT(*) FROM vendors")
    suspend fun getCountVendors(): Int

}
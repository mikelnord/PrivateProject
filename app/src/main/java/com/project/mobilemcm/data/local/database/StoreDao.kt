package com.project.mobilemcm.data.local.database

import androidx.room.*
import com.project.mobilemcm.data.local.database.model.StoreItem
import com.project.mobilemcm.data.local.database.model.Store

@Dao
interface StoreDao {

    @Query("SELECT * FROM store order by name")
    suspend fun getAll(): List<Store>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stores: List<Store>)

    @Query("SELECT id,name FROM store where division_id=:divisionId")
    suspend fun getStoresFromDivision(divisionId:String):List<StoreItem>?

    @Delete
    suspend fun delete(store: Store)

    @Query("DELETE FROM store")
    suspend fun clearStore()

    @Query("SELECT COUNT(*) FROM STORE")
    suspend fun getCountStore(): Int

}
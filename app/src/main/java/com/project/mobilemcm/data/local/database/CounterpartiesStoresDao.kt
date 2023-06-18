package com.project.mobilemcm.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.mobilemcm.data.local.database.model.CounterpartiesStores

@Dao
interface CounterpartiesStoresDao {

    @Query("SELECT * FROM counterpartiesstores where company=:idCompany and " +
            "address LIKE '%' || :name || '%' and deletionmark=0 order by name")
    suspend fun getAdressFromCompany(idCompany: String, name: String): List<CounterpartiesStores>

    @Query("SELECT * FROM counterpartiesstores where id=:idAdr and " +
            "deletionmark=0 order by name")
    suspend fun getAdresCompany(idAdr: String):CounterpartiesStores?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(counterpartiesStores: CounterpartiesStores)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(counterpartiesStoresList: List<CounterpartiesStores>)

    @Delete
    suspend fun delete(counterpartiesStores: CounterpartiesStores)

    @Query("DELETE FROM counterpartiesstores")
    suspend fun clearStore()

    @Query("SELECT COUNT(*) FROM counterpartiesstores")
    suspend fun getCountCounterpartiesStores(): Int

}
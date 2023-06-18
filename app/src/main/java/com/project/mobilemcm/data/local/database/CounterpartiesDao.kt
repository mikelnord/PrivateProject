package com.project.mobilemcm.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.mobilemcm.data.local.database.model.Counterparties
import kotlinx.coroutines.flow.Flow

@Dao
interface CounterpartiesDao {
    @Query("SELECT * FROM counterparties order by name")
    suspend fun getAll(): List<Counterparties>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(counterparties: Counterparties)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(counterpartiesList: List<Counterparties>)

    @Delete
    suspend fun delete(counterparties: Counterparties)

    @Query("DELETE FROM counterparties")
    suspend fun clearCounterparties()

    @Query("SELECT COUNT(*) FROM counterparties")
    suspend fun getCountCounterparties(): Int

    @Query("SELECT * FROM counterparties where name LIKE '%' || :name || '%' and deletionmark=0 order by name")
    fun getCompanies(name: String): Flow<List<Counterparties>>

    @Query("SELECT * FROM counterparties where user=:id and name LIKE '%' || :name || '%' and deletionmark=0 order by name")
    fun getMyCompanies(name: String, id: String): Flow<List<Counterparties>>

    @Query("""Select * from counterparties where id=:id""")
    suspend fun getCompany(id: String): Counterparties
}
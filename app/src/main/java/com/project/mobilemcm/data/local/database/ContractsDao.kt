package com.project.mobilemcm.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.mobilemcm.data.local.database.model.Contract

@Dao
interface ContractsDao {

    @Query("SELECT * FROM contract order by name")
    suspend fun getAll(): List<Contract>

    @Query("SELECT * FROM contract WHERE company=:company order by name")
    suspend fun getCompanyContract(company: String): List<Contract>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contracts: List<Contract>)

    @Delete
    suspend fun delete(contract: Contract)

    @Query("DELETE FROM contract")
    suspend fun clearContracts()

    @Query("SELECT COUNT(*) FROM Contract")
    suspend fun getCountStore(): Int
}
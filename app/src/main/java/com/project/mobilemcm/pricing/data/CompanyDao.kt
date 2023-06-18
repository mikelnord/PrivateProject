package com.project.mobilemcm.pricing.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.mobilemcm.pricing.model.Company

@Dao
interface CompanyDao {

    @Query("SELECT * FROM company ")
    suspend fun getAll(): List<Company>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(company: List<Company>)

    @Delete
    suspend fun delete(company: Company)

    @Query("DELETE FROM company")
    suspend fun clearCompany()

    @Query("SELECT COUNT(*) FROM COMPANY")
    suspend fun getCountCompany(): Int

}
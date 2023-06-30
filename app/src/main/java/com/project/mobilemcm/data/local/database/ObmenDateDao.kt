package com.project.mobilemcm.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.mobilemcm.data.local.database.model.ObmenDate
import kotlinx.coroutines.flow.Flow

@Dao
interface ObmenDateDao {
    @Query("SELECT * FROM ObmenDate ")
    suspend fun getDate(): ObmenDate?

    @Query("SELECT * FROM ObmenDate ")
    fun getFlowDate(): Flow<ObmenDate?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(obmenDate: ObmenDate)

    @Delete
    suspend fun delete(obmenDate: ObmenDate)

    @Query("DELETE FROM ObmenDate")
    suspend fun clearObmenDate()

    @Query("SELECT COUNT(*) FROM ObmenDate")
    suspend fun getCountObmenDate(): Int?
}

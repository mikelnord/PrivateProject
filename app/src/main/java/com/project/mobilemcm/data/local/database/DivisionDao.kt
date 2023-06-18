package com.project.mobilemcm.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.mobilemcm.data.local.database.model.Division


@Dao
interface DivisionDao {
    @Query("SELECT * FROM Division order by name")
    suspend fun getAll(): List<Division>

    @Query("SELECT name FROM Division WHERE id=:id")
    suspend fun getDivisionByID(id:String):String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(divisions: List<Division>)

    @Delete
    suspend fun delete(division: Division)

    @Query("DELETE FROM division")
    suspend fun clearDivision()


    @Query("SELECT COUNT(*) FROM division")
    suspend fun getCountDivisions(): Int

}
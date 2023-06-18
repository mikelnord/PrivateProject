package com.project.mobilemcm.pricing.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.mobilemcm.pricing.model.ItemAction


@Dao
interface ItemActionDao {

    @Query("SELECT * FROM itemaction ")
    suspend fun getAll(): List<ItemAction>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(item: List<ItemAction>?)

    @Delete
    suspend fun delete(itemAction: ItemAction)

    @Query("DELETE FROM ItemAction")
    suspend fun clearItemAction()

    @Query("SELECT COUNT(*) FROM ItemAction")
    suspend fun getCountItemAction(): Int

}
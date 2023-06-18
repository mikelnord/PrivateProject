package com.project.mobilemcm.data.local.database

import androidx.room.*
import com.project.mobilemcm.data.local.database.model.Stock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface StockDao {

    @Query("SELECT * FROM stock")
    fun getAll(): Flow<List<Stock>>

    fun getAllDistinct() = getAll().distinctUntilChanged()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stock: List<Stock>)

    @Delete
    suspend fun delete(stock: Stock)

    @Query("DELETE FROM stock")
    suspend fun clearStock()

    @Query("SELECT COUNT(*) FROM stock")
    suspend fun getCountStock(): Int

    @MapInfo(keyColumn = "good_id", valueColumn = "amount")
    @Query("""Select good_id, amount from stock where store_id=:store_id and good_id IN (:list_id)""")
    suspend fun getMapAmount(store_id: String , list_id:List<String>): Map<String, Double>
}
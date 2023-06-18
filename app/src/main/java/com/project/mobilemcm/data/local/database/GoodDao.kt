package com.project.mobilemcm.data.local.database

import androidx.room.*
import com.project.mobilemcm.data.local.database.model.Good
import com.project.mobilemcm.data.local.database.model.GoodWithStock
import com.project.mobilemcm.data.local.database.model.Vendors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface GoodDao {

    @Query("SELECT * FROM good order by name")
    fun getAll(): Flow<List<Good>>

    fun getAllDistinct() = getAll().distinctUntilChanged()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(goods: List<Good>)

    @Delete
    suspend fun delete(good: Good)

    @Query("DELETE FROM good")
    suspend fun clearGood()

    @Query("SELECT COUNT(*) FROM GOOD")
    suspend fun getCountGoods(): Int

    @Query(
        """Select g.id, g.name, g.vendorCode ,s.amount, s.price, g.pricegroup, g.pricegroup2, g.category, 
            g.vendor as vendors, p.name as namePricegroup, 0 as count
           from  Good g 
           LEFT JOIN pricegroup p on g.pricegroup=p.id
           LEFT JOIN stock s  on g.id=s.good_id and  s.store_id=:store
           where g.category=:id and deletionmark=0
           ORDER BY g.name"""
    )
    fun getChildGoods(
        id: String,
        store: String
    ): Flow<List<GoodWithStock>>


    @Query(
        """SELECT g. id, g.name, g.vendorCode ,s.amount, s.price, g.pricegroup, g.pricegroup2, g.category, 
            g.vendor as vendors, p.name as namePricegroup, 0 as count
  FROM good g
  LEFT JOIN pricegroup p on g.pricegroup=p.id
  LEFT JOIN stock s  on g.id=s.good_id and  s.store_id=:store
  WHERE (g.name like :query or g.vendorCode like :query) and g.deletionmark=0 
  ORDER BY g.name"""
    )
    fun search(
        query: String,
        store: String = "c3a21002-ef22-11e5-a605-f07959941a7c"
    ): Flow<List<GoodWithStock>>

    @Query(
        """SELECT g. id, g.name, g.vendorCode ,s.amount, s.price, g.pricegroup, g.pricegroup2, g.category, 
            g.vendor as vendors, p.name as namePricegroup, 0 as count
  FROM good g
  LEFT JOIN pricegroup p on g.pricegroup=p.id
  LEFT JOIN stock s  on g.id=s.good_id and  s.store_id=:store
  WHERE g.deletionmark=0 
  ORDER BY g.name"""
    )
    fun fullList(
        store: String = "c3a21002-ef22-11e5-a605-f07959941a7c"
    ): Flow<List<GoodWithStock>>

    @Query("""Select distinct ""as vendor_id ,vendor as name from good where deletionmark=0 and vendor!=""order by vendor""")
    suspend fun allVendors(): List<Vendors>


}
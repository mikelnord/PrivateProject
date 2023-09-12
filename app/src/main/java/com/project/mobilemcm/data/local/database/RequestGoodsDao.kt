package com.project.mobilemcm.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.mobilemcm.data.local.database.model.GoodWithStock
import com.project.mobilemcm.data.local.database.model.RequestGoods

@Dao
interface RequestGoodsDao {
    @Query(
        """Select g.id, g.name,g.vendorCode, 0 as amount , rg.price, g.pricegroup, g.pricegroup2, g.category, 
           g.vendor as vendors, "" as namePricegroup, rg.count, rg.number, rg.discont, rg.metod, rg.priceInd, 0 as reserve
           from requestgoods rg inner join requestdocument r on rg.idDoc=r.document_id
           inner join good g on rg.idGood=g.id
           where r.document_id=:idDoc"""
    )
    suspend fun goodsDoc(idDoc: Long): List<GoodWithStock>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(requestGoods: RequestGoods)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(requestGoodsList: List<RequestGoods>)

    @Delete
    suspend fun delete(requestGoods: RequestGoods)

    @Query("DELETE FROM RequestGoods where idDoc=:idDoc")
    suspend fun clearGoodsDocuments(idDoc: Long)
}
package com.project.mobilemcm.pricing.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.mobilemcm.pricing.logic.DiscountCompany
import com.project.mobilemcm.pricing.model.Disconts

@Dao
interface DiscontsDao {

    @Query("SELECT * FROM Disconts ")
    suspend fun getAll(): List<Disconts>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(disconts: List<Disconts>)

    @Delete
    suspend fun delete(disconts: Disconts)

    @Query("DELETE FROM Disconts")
    suspend fun clearDisconts()

    @Query("SELECT COUNT(*) FROM DISCONTS")
    suspend fun getCountDicsonts(): Int

    @Query(
        """select max(Disconts.date_start) as date , pricegroup.name, discount  from Disconts join item on Disconts.id=item.id
JOIN pricegroup on Item.pricegroup=pricegroup.id
where ((date(:date)>=date(date_start) and date(:date)<=date(date_end)) or  (date(:date)>=date(date_start) and date_end="")) and active='true'
and Disconts.id in (select id from company where company.company_id=:company_id)
GROUP BY pricegroup.name
ORDER BY pricegroup.name"""
    )
    suspend fun getDiscontsFromCompany(company_id: String, date: String):List<DiscountCompany>?

}
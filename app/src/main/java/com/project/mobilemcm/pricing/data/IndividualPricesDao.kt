package com.project.mobilemcm.pricing.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.mobilemcm.pricing.logic.IndPrices
import com.project.mobilemcm.pricing.model.IndividualPrices


@Dao
interface IndividualPricesDao {

    @Query("SELECT * FROM IndividualPrices ")
    suspend fun getAll(): List<IndividualPrices>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(individualPrices: List<IndividualPrices>)

    @Delete
    suspend fun delete(individualPrices: IndividualPrices)

    @Query("DELETE FROM IndividualPrices")
    suspend fun clearIndividualPrices()

    @Query("SELECT COUNT(*) FROM INDIVIDUALPRICES")
    suspend fun getCountIndividualPrices(): Int

    @Query(
        """select p.number, i.price, 0 as discount, 1 as metod from individualprices p  join itemind i on p.id=i.id and i.good_id=:good_id
where active='true' and date(:date)>=date(date_start) and date(:date)<=date(date_end) and p.company_id=:company_id"""
    )
    suspend fun getIndividualPices(good_id: String, company_id: String, date: String): IndPrices?

    @Query(
        """select a.number, i.price, 0 as discount, 3 as metod from actionprices a inner join itemaction i on a.id=i.id and( i.division_id=:division_id and  i.good_id=:good_id)
where  date(:date)>=date(date_start) and date(:date)<=date(date_end)
and active='true' and type="ЦеноваяАкция""""
    )
    suspend fun getActionPricesOther(division_id: String, good_id: String, date: String): IndPrices?

    @Query(
        """select a.number, i.price, 0 as discount, 2 as metod from actionprices a inner join itemaction i on a.id=i.id and( i.division_id=:division_id and  i.good_id=:good_id)
where  date(:date)>=date(date_start) and date(:date)<=date(date_end)
and active='true' and type="РаспродажаДляИнтернетМагазинов""""
    )
    suspend fun getActionPricesIm(division_id: String, good_id: String, date: String): IndPrices?

    @Query(
        """select  number, 0 as price, discount, 4 as metod  from Disconts join item on Disconts.id=item.id and ((pricegroup=:pricegroup and pricegroup2=:pricegroup2) 
or pricegroup=:pricegroup or good_id=:good_id)
where ( (date(:date)>=date(date_start) and date(:date)<=date(date_end)) or  (date(:date)>=date(date_start) and date_end="")) and active='true'
and Disconts.id in (select id from company where company.company_id=:company_id)
ORDER BY date_start DESC
LIMIT 1"""
    )
    suspend fun getDisconts(
        company_id: String,
        date: String,
        pricegroup: String,
        pricegroup2: String,
        good_id: String
    ): IndPrices?

    @Query("""Select internet from counterparties where id=:id""")
    suspend fun getIm(id: String): Boolean?

    @Query("""select document_id from requestdocument order by document_id desc limit 1""")
    suspend fun getLastDocid():Long?
}
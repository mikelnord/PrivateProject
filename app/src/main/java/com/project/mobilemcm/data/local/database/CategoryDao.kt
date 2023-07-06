package com.project.mobilemcm.data.local.database

import androidx.room.*
import com.project.mobilemcm.data.local.database.model.Category
import com.project.mobilemcm.data.local.database.model.DomainCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface CategoryDao {

    @Query("SELECT * FROM category order by name")
    fun getAll(): Flow<List<Category>>

    fun getAllDistinct() = getAll().distinctUntilChanged()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categorys: List<Category>)

    @Delete
    suspend fun delete(category: Category)

    @Query("DELETE FROM category")
    suspend fun clearCategory()

    @Query("SELECT COUNT(*) FROM category")
    suspend fun getCountCategory(): Int

    @Query(
        """Select  c.id, c.parent_id, cat.name, cat.code, st.amount, g.pricegroup, g.vendor from good g 
left join  stock st on st.good_id=g.id and st.store_id=:store
left join category c on g.category=c.id and c.deletionmark=0
join category cat on c.parent_id=cat.id
where g.deletionmark=0 order by c.code"""
    )
    fun categoryBaseList(store: String): Flow<List<DomainCategory>>

    @Query(
        """Select c.id, c.parent_id, c.name, c.code, st.amount, g.pricegroup, g.vendor from good g  
left join stock st on g.id =st.good_id and st.store_id=:store
 inner join category c on g.category=c.id and c.parent_id=:parent
where g.deletionmark=0 order by c.name"""
    )
    suspend fun categoryChildList(
        parent: String,
        store: String
    ): List<DomainCategory>

}
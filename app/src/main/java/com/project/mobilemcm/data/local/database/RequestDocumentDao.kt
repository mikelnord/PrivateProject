package com.project.mobilemcm.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.mobilemcm.data.local.database.model.RequestDocument
import com.project.mobilemcm.data.local.database.model.RequestDocumentItem

@Dao
interface RequestDocumentDao {

    @Query("""Select r.document_id, r.docDate, s.name as nameStore, c.name as nameCounterparties from requestdocument r left join store s on r.store_id=s.id
            left join counterparties c on r.counterparties_id=c.id   order by docDate""")
    suspend fun getAll(): List<RequestDocumentItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(requestDocument: RequestDocument):Long

    @Delete
    suspend fun delete(requestDocument: RequestDocument)

    @Query("DELETE FROM RequestDocument")
    suspend fun clearDocuments()

    @Query("""Select * from requestdocument where document_id=:id""")
    suspend fun getDocument(id:Long) : RequestDocument
}
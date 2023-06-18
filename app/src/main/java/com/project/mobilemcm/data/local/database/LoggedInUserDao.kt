package com.project.mobilemcm.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.mobilemcm.data.local.database.model.LoggedInUser

@Dao
interface LoggedInUserDao {
    @Query("SELECT * FROM loggedinuser where isActive=0")
    suspend fun getActiveUser(): LoggedInUser?

    @Query("SELECT * FROM loggedinuser where isActive=1")
    suspend fun getInactiveUser(): LoggedInUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(loggedInUser: LoggedInUser)

    @Query("DELETE FROM loggedinuser where id<>:id")
    suspend fun clearUser(id: String)


    @Query("SELECT COUNT(*) FROM loggedinuser")
    suspend fun getCountUser(): Int

    @Query("""Update loggedinuser set isActive=1""")
    suspend fun setUserInactive()

    @Query("""Update loggedinuser set timeWork=:timeWork where id=:id""")
    suspend fun setUserTimeWork(id: String, timeWork: Long)

}
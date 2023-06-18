package com.project.mobilemcm.data.login

import com.project.mobilemcm.data.local.database.LoggedInUserDao
import com.project.mobilemcm.data.local.database.model.LoggedInUser
import com.project.mobilemcm.data.local.database.model.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(
    private val dataSource: LoginDataSource,
    private val loggedInUserDao: LoggedInUserDao
) {


    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            setInactiveUser()
        }
    }

    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        // handle login
        val result = dataSource.login(username, password)

        if (result.status == Result.Status.SUCCESS) {
            result.data?.let {
                setLoggedInUser(it)
                return result
            }

        }
        if (result.status == Result.Status.ERROR) {
            return Result.error(result.message.toString(), result.error)
        }

        return Result.error("", null)
    }

    fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
    }

    suspend fun getActiveUser() = loggedInUserDao.getActiveUser()

    suspend fun getInactiveUser() = loggedInUserDao.getInactiveUser()

    suspend fun insertUser(loggedInUser: LoggedInUser) {
        loggedInUserDao.insert(loggedInUser)
        loggedInUserDao.clearUser(loggedInUser.id)
    }

    suspend fun countUser() = loggedInUserDao.getCountUser()

    private suspend fun setInactiveUser() = loggedInUserDao.setUserInactive()

    suspend fun setUserTimeWork(id: String, timeWork: Long) =
        loggedInUserDao.setUserTimeWork(id, timeWork)
}
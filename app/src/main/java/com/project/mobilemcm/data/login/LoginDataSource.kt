package com.project.mobilemcm.data.login

import com.project.mobilemcm.data.local.database.model.LoggedInUser
import com.project.mobilemcm.data.local.database.model.Result
import com.project.mobilemcm.data.remote.RemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject


class LoginDataSource @Inject constructor(private val remoteDataSource: RemoteDataSource) {

    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        val listUser = coroutineScope {
            async(Dispatchers.IO) {
                remoteDataSource.getUserList()
            }
        }
        val resultList = listUser.await()
        if (resultList.status == Result.Status.SUCCESS) {
            resultList.data?.let {
                val loggedUserList =
                    it.users.filter { user -> user.login == username && user.password == password }
                if (loggedUserList.isNotEmpty()) {

                    val loggedInUser = with(loggedUserList.first()) {
                        LoggedInUser(
                            id,
                            division_id,
                            name,
                            login,
                            password,
                            System.currentTimeMillis()
                        )
                    }
                    return Result.success(loggedInUser)
                }
            }
        }
        if (resultList.status == Result.Status.ERROR) {
            return Result.error(resultList.message.toString(), resultList.error)
        }
        return Result.error("", null)
    }

    fun logout() {
        // TODO: revoke authentication
    }
}
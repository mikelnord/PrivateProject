package com.project.mobilemcm.data.remote

import android.util.Log
import com.project.mobilemcm.data.local.database.model.AnswerServer
import com.project.mobilemcm.data.local.database.model.CompanyInfo
import com.project.mobilemcm.data.local.database.model.FileObmen
import com.project.mobilemcm.data.local.database.model.FileUsers
import com.project.mobilemcm.data.local.database.model.RequestDocument1c
import com.project.mobilemcm.data.remote.network.ApiService
import retrofit2.Response
import javax.inject.Inject
import com.project.mobilemcm.data.local.database.model.Result
import com.project.mobilemcm.data.local.database.model.UpdateDate
import com.project.mobilemcm.util.ErrorUtils
import retrofit2.Retrofit


class RemoteDataSource @Inject constructor(
    private val apiService: ApiService,
    private val retrofit: Retrofit
) {
    private suspend fun <T> getResponse(
        request: suspend () -> Response<T>,
        defaultErrorMessage: String
    ): Result<T> {
        return try {
            val result = request.invoke()
            if (result.isSuccessful) {
                return Result.success(result.body())
            } else {
                val errorResponse = ErrorUtils.parseError(result, retrofit)
                Log.e("errorObmen", errorResponse?.status_message ?: defaultErrorMessage)
                Result.error(errorResponse?.status_message ?: defaultErrorMessage, errorResponse)
            }
        } catch (e: Throwable) {
            Log.e("errorObmen", e.message.toString())
            Result.error(e.message.toString(), null)
        }
    }

    suspend fun startObmen(strDate: String, strPodr: String, strUserId: String): Result<FileObmen> {
        return getResponse(
            request = {
                apiService.getObmen(
                    strDate = strDate,
                    strPodr = strPodr,
                    strUserId = strUserId
                )
            },
            defaultErrorMessage = "Error start obmen"
        )
    }

    suspend fun getUserList(): Result<FileUsers> {
        return getResponse(
            request = { apiService.getUsers() },
            defaultErrorMessage = "Error get users"
        )
    }

    suspend fun postDoc(requestDocument1c: RequestDocument1c): Result<AnswerServer?> {
        return getResponse(
            request = { apiService.postDoc(requestDocument1c) },
            defaultErrorMessage = "Error post doc"
        )
    }

    suspend fun getCompanyInfo(companyId: String): Result<CompanyInfo?> {
        return getResponse(
            request = { apiService.getCompanyInfo(companyId) },
            defaultErrorMessage = "Error get info company"
        )
    }

    suspend fun getVersionInfo(): Result<UpdateDate> {
        return getResponse(
            request = { apiService.getVersionInfo() },
            defaultErrorMessage = "Error get version info"
        )
    }
}
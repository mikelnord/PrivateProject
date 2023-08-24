package com.project.mobilemcm.data.remote.network

import com.project.mobilemcm.data.local.database.model.AnswerServer
import com.project.mobilemcm.data.local.database.model.CompanyInfo
import com.project.mobilemcm.data.local.database.model.FileObmen
import com.project.mobilemcm.data.local.database.model.FileUsers
import com.project.mobilemcm.data.local.database.model.RequestDocument1c
import com.project.mobilemcm.data.local.database.model.UpdateDate
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("exchange/{strPodr}/{strDate}/{strUserId}")
    suspend fun getObmen(
        @Path("strDate") strDate: String,
        @Path("strPodr") strPodr: String,
        @Path("strUserId") strUserId: String
    ): Response<FileObmen>

    @GET("getusers")
    suspend fun getUsers(): Response<FileUsers>

    @GET("get_version_аpplication")
    suspend fun getVersionInfo(): Response<UpdateDate>

    @POST("place_order")
    suspend fun postDoc(@Body requestDocument1c: RequestDocument1c): Response<AnswerServer>

    @GET("get_balans/{companyId}")
    suspend fun getCompanyInfo(
        @Path("companyId") companyId: String
    ): Response<CompanyInfo>

}
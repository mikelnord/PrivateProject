package com.project.mobilemcm.data.remote.network

import com.project.mobilemcm.BuildConfig
import com.project.mobilemcm.data.local.database.model.AnswerEmail
import com.project.mobilemcm.data.local.database.model.AnswerServer
import com.project.mobilemcm.data.local.database.model.CompanyInfo
import com.project.mobilemcm.data.local.database.model.Debets
import com.project.mobilemcm.data.local.database.model.FileObmen
import com.project.mobilemcm.data.local.database.model.FileUsers
import com.project.mobilemcm.data.local.database.model.Payments
import com.project.mobilemcm.data.local.database.model.RequestDocument1c
import com.project.mobilemcm.data.local.database.model.UpdateDate
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("exchange/{strPodr}/{strDate}/{strUserId}/{NumberVersion}/{UUIDInstall}")
    suspend fun getObmen(
        @Path("strDate") strDate: String,
        @Path("strPodr") strPodr: String,
        @Path("strUserId") strUserId: String,
        @Path("NumberVersion") numberVersion: String = BuildConfig.VERSION_NAME,
        @Path("UUIDInstall") uuidInstall: String = "11111111",
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

    @GET("get_debets/{strUserId}")
    suspend fun getDebets(
        @Path("strUserId") strUserId: String
    ): Response<Debets>

    @GET("get_payments/{strUserId}")
    suspend fun getPayments(
        @Path("strUserId") strUserId: String
    ): Response<Payments>

    @GET("send_bill/{docId}/{email}")
    suspend fun postEmail(
        @Path("docId") docId: String,
        @Path("email") email: String
    ): Response<AnswerEmail?>


//    дебиторка /upr_mcm/hs/MobileAgent/get_debets/cab6be6e-4f74-11e5-80e4-001e67921ce7
//    платежи /upr_mcm/hs/MobileAgent/get_payments/cab6be6e-4f74-11e5-80e4-001e67921ce7
//    счет /upr_mcm/hs/MobileAgent/send_bill/1583c863-931c-405f-bafb-f2d193566bee/obuhov@wlbs.ru
}
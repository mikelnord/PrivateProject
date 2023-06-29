package com.project.mobilemcm.data.remote.network

import com.project.mobilemcm.data.local.database.model.AnswerServer
import com.project.mobilemcm.data.local.database.model.FileObmen
import com.project.mobilemcm.data.local.database.model.FileUsers
import com.project.mobilemcm.data.local.database.model.RequestDocument1c
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

    @POST("place_order")
    suspend fun postDoc(@Body requestDocument1c: RequestDocument1c): Response<AnswerServer>
}
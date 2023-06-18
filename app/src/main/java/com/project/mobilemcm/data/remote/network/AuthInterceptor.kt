package com.project.mobilemcm.data.remote.network

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor(user: String, password: String) :
    Interceptor {
    private val credentials: String

    init {
        credentials = Credentials.basic(user, password)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val authenticatedRequest: Request = request.newBuilder()
            .addHeader("Authorization", credentials)
            .build()
        return chain.proceed(authenticatedRequest)
    }
}
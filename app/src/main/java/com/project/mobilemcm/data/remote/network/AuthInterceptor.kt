package com.project.mobilemcm.data.remote.network

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import javax.inject.Inject

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

class NullOnEmptyConverterFactory @Inject constructor() : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ) = Converter<ResponseBody, Any?> {
        if (it.contentLength() != 0L) retrofit.nextResponseBodyConverter<Any?>(
            this,
            type,
            annotations
        ).convert(it) else null
    }
}
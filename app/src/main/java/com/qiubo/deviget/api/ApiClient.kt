package com.qiubo.deviget.api

import com.qiubo.deviget.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private val public by lazy {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val builderClient = OkHttpClient.Builder()
        builderClient.addInterceptor(interceptor)

        Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(builderClient.build())
            .build()
    }

    private fun <T> getService(serviceClass: Class<T>): T = public.create(serviceClass)

    fun getRedditService() = getService(IRedditService::class.java)
}
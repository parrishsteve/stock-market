package com.parrishsystems.stock.repo.rest_api

import com.parrishsystems.stock.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


open class BaseService {
    protected val BASE_URL = BuildConfig.STOCK_BASE_URL
    protected val API_KEY = BuildConfig.STOCK_API_KEY
    private val client = RetrofitClient(BASE_URL, BuildConfig.DEBUG).create()
    protected val service = client.create(Apis::class.java)
}
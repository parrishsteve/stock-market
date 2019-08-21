package com.parrishsystems.stock.repo.rest_api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



class RetrofitClient(val baseUrl: String, val includeLogging: Boolean) {
    fun create() : Retrofit {
        val client: OkHttpClient
        if (includeLogging) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
        }
        else {
            client = OkHttpClient.Builder()
                .build()
        }

        return retrofit2.Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
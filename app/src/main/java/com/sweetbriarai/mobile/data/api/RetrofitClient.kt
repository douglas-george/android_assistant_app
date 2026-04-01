package com.sweetbriarai.mobile.data.api

import com.sweetbriarai.mobile.BuildConfig
import com.sweetbriarai.mobile.data.auth.AuthManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    fun create(authManager: AuthManager): MobileApiService {
        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Authorization", "Bearer ${authManager.bearerToken}")
                .build()
            chain.proceed(request)
        }

        val clientBuilder = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            clientBuilder.addInterceptor(loggingInterceptor)
        }

        return Retrofit.Builder()
            .baseUrl(authManager.apiUrl)
            .client(clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MobileApiService::class.java)
    }
}

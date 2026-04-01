package com.sweetbriarai.mobile.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MobileApiService {
    @POST("devices/register")
    suspend fun registerDevice(@Body request: RegisterDeviceRequest): RegisterDeviceResponse

    @GET("messages")
    suspend fun getMessages(
        @Query("status") status: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("since") since: String? = null
    ): MessageListResponse

    @GET("messages/{id}")
    suspend fun getMessage(@Path("id") id: Int): MobileMessage

    @POST("messages/{id}/respond")
    suspend fun respondToMessage(
        @Path("id") id: Int,
        @Body request: RespondRequest
    ): StatusResponse

    @POST("messages/{id}/read")
    suspend fun markRead(@Path("id") id: Int): StatusResponse

    @GET("health")
    suspend fun health(): StatusResponse
}
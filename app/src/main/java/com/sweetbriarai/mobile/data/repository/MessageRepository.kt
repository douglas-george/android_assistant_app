package com.sweetbriarai.mobile.data.repository

import com.sweetbriarai.mobile.data.api.MobileApiService
import com.sweetbriarai.mobile.data.api.RegisterDeviceRequest
import com.sweetbriarai.mobile.data.api.RespondRequest
import com.sweetbriarai.mobile.data.api.StatusResponse
import com.sweetbriarai.mobile.data.auth.AuthManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MessageRepository(
    private val authManager: AuthManager,
    private val apiService: MobileApiService
) {

    suspend fun registerDevice(request: RegisterDeviceRequest) =
        withContext(Dispatchers.IO) {
            apiService.registerDevice(request)
        }

    suspend fun getMessages(status: String? = null, limit: Int = 20, since: String? = null) =
        withContext(Dispatchers.IO) {
            apiService.getMessages(status, limit, since)
        }

    suspend fun getMessage(id: Int) = withContext(Dispatchers.IO) {
        apiService.getMessage(id)
    }

    suspend fun respondToMessage(id: Int, response: String): StatusResponse =
        withContext(Dispatchers.IO) {
            apiService.respondToMessage(id, RespondRequest(response))
        }

    suspend fun markRead(id: Int): StatusResponse = withContext(Dispatchers.IO) {
        apiService.markRead(id)
    }

    suspend fun health(): StatusResponse = withContext(Dispatchers.IO) {
        apiService.health()
    }
}
package com.sweetbriarai.mobile.data.api

data class RegisterDeviceRequest(
    val device_name: String,
    val device_type: String = "phone",
    val fcm_token: String,
    val app_version: String
)

data class RegisterDeviceResponse(val device_id: Int, val status: String)
data class StatusResponse(val status: String)
data class RespondRequest(val response_value: String)
data class MessageListResponse(val messages: List<MobileMessage>)

data class MobileMessage(
    val id: Int,
    val sender_label: String,
    val message_text: String,
    val message_type: String,
    val response_options: List<String>?,
    val priority: String,
    val status: String,
    val response_value: String?,
    val created_at: String
)
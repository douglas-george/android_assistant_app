package com.sweetbriarai.mobile.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sweetbriarai.mobile.data.api.MobileMessage
import com.sweetbriarai.mobile.data.api.RetrofitClient
import com.sweetbriarai.mobile.data.auth.AuthManager
import com.sweetbriarai.mobile.data.repository.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MessageViewModel(application: Application) : AndroidViewModel(application) {

    private val authManager = AuthManager(application)
    private val apiService = RetrofitClient.create(authManager)
    private val repository = MessageRepository(authManager, apiService)

    private val _messages = MutableStateFlow<List<MobileMessage>>(emptyList())
    val messages: StateFlow<List<MobileMessage>> = _messages

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _selectedMessage = MutableStateFlow<MobileMessage?>(null)
    val selectedMessage: StateFlow<MobileMessage?> = _selectedMessage

    private val _isResponding = MutableStateFlow(false)
    val isResponding: StateFlow<Boolean> = _isResponding

    fun refreshMessages() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val response = repository.getMessages()
                _messages.value = response.messages
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun loadMessage(id: Int) {
        viewModelScope.launch {
            try {
                _selectedMessage.value = repository.getMessage(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun respondToMessage(id: Int, response: String) {
        viewModelScope.launch {
            _isResponding.value = true
            try {
                repository.respondToMessage(id, response)
                _selectedMessage.value = _selectedMessage.value?.copy(
                    response_value = response,
                    status = "responded"
                )
                // Refresh list to reflect updated status
                refreshMessages()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isResponding.value = false
            }
        }
    }
}

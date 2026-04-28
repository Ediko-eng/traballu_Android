package com.example.howtobeamillionaire.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.howtobeamillionaire.data.entity.UserEntity
import com.example.howtobeamillionaire.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<Result<UserEntity>?>(null)
    val loginState: StateFlow<Result<UserEntity>?> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<Result<Unit>?>(null)
    val registerState: StateFlow<Result<Unit>?> = _registerState.asStateFlow()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.login(email, password)
            _loginState.value = result
            if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
            }
        }
    }

    fun register(name: String, age: Int, gender: String, email: String, password: String) {
        viewModelScope.launch {
            val user = UserEntity(name = name, age = age, gender = gender, email = email, password = password)
            val result = repository.registerUser(user)
            _registerState.value = result
        }
    }

    fun logout() {
        _currentUser.value = null
        _loginState.value = null
    }

    fun clearErrors() {
        _loginState.value = null
        _registerState.value = null
    }
}

class LoginViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(UserRepository(context)) as T
    }
}
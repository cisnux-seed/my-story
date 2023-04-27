package dev.cisnux.mystory.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cisnux.mystory.domain.AuthRepository
import dev.cisnux.mystory.domain.UserAuth
import dev.cisnux.mystory.utils.ApplicationState
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AuthRepository) :
    ViewModel() {
    private val _registerState = MutableLiveData<ApplicationState<String>>()
    val registerState: LiveData<ApplicationState<String>> get() = _registerState

    private var _loginState = MutableLiveData<ApplicationState<String>>()
    val loginState: LiveData<ApplicationState<String>>
        get() = _loginState

    private val _isAlreadyLogin = MutableLiveData<String?>()
    val isAlreadyLogin: LiveData<Boolean> = _isAlreadyLogin.map {
        it != null
    }

    init {
        getLoginSession()
    }

    fun register(name: String, email: String, password: String) = viewModelScope.launch {
        val userAuth = UserAuth(name, email, password)
        repository.register(userAuth).collect {
            _registerState.value = it
        }
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        val userAuth = UserAuth(email, password)
        repository.login(userAuth).collect {
            _loginState.value = it
        }
    }

    fun logout() = viewModelScope.launch {
        _isAlreadyLogin.value = null
        repository.logout()
    }

    private fun getLoginSession() = viewModelScope.launch {
        _isAlreadyLogin.value = repository.isAlreadyLogin()
    }
}
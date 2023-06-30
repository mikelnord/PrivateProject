package com.project.mobilemcm.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.mobilemcm.R
import com.project.mobilemcm.data.Repository
import com.project.mobilemcm.data.local.database.model.Result
import com.project.mobilemcm.data.login.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val repository: Repository
) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult?>()
    val loginResult: LiveData<LoginResult?> = _loginResult

    init {
        viewModelScope.launch {
            if (loginRepository.countUser() != 0) {
                loginRepository.getActiveUser()?.let {
                    _loginResult.value = LoginResult(success = LoggedInUserView(it.displayName))
                    loginRepository.setLoggedInUser(it)
                }
            }
        }
    }

    private fun tickerFlow(period: Long) = flow {
        val loggedInUser = viewModelScope.async {
            loginRepository.getActiveUser()
        }
        val timeEnd = 24 * 3600 * 1000 - (loggedInUser.await()?.timeWork ?: 0)
        val timeInterval = 1000 * 60 * 1L
        while (true) {
            loggedInUser.await()?.let { emit(10) }
            delay(1000)
        }
    }


    fun login(username: String, password: String) {
        viewModelScope.launch {
            val result = loginRepository.login(username, password)
            if (result.status == Result.Status.SUCCESS) {
                _loginResult.postValue(
                    LoginResult(success = loginRepository.user?.let { LoggedInUserView(displayName = it.displayName) })
                )
                loginRepository.getInactiveUser()?.let {
                    loginRepository.user?.division_id?.let {userDivisionId->
                        if (it.division_id != userDivisionId) {
                            viewModelScope.launch(Dispatchers.IO) {
                                //repository.clearBase()
                             }
                        }
                    }
                }
                loginRepository.user?.let { user -> loginRepository.insertUser(user) }
                //startTimer()
            } else {
                _loginResult.postValue(LoginResult(error = R.string.login_failed))
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String) =
        username.isNotBlank()


    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 2
    }

    fun logout() {
        _loginResult.value = null
        loginRepository.logout()
    }
}
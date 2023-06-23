package com.project.mobilemcm.ui.home

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.mobilemcm.data.Repository
import com.project.mobilemcm.data.login.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository,
    val loginRepository: LoginRepository
) : ViewModel() {

    private var _appMode: MutableLiveData<Boolean> =
        MutableLiveData(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
    val appMode = _appMode

    private var _emptyBase: MutableLiveData<Boolean> = MutableLiveData()
    val emptyBase = _emptyBase

    fun isLoginFirst() {
        viewModelScope.launch {
            _emptyBase.postValue(repository.firstLogin() == 0)
        }
    }


    fun setMode() {
        appMode.value?.let {
            if (it) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            _appMode.value = !it
        }
    }

}
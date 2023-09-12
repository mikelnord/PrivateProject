package com.project.mobilemcm.ui.reports

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.mobilemcm.data.Repository
import com.project.mobilemcm.data.local.database.model.DebetItem
import com.project.mobilemcm.data.local.database.model.Result
import com.project.mobilemcm.data.login.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DebetViewModel @Inject constructor(
    private val repository: Repository,
    private val loginRepository: LoginRepository
) : ViewModel() {

    init {
        getDebet()
    }

    private var _debetList = MutableLiveData<List<DebetItem>?>()
    val debetList = _debetList

    private fun getDebet() {
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.user?.let {
                val result = repository.getDebets(it.id)
                result.let { res ->
                    if (res.status == Result.Status.SUCCESS) {
                        _debetList.postValue(result.data?.debets)
                    }
                }
            }
        }
    }


}
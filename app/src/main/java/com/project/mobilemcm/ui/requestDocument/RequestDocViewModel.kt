package com.project.mobilemcm.ui.requestDocument

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.project.mobilemcm.data.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RequestDocViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private var _queryCompanies = MutableLiveData<String>()
    private val queryCompanies = _queryCompanies

    fun setQueryCompanies(query: String) {
        _queryCompanies.value = query
    }

    val listCompanies=queryCompanies.switchMap {
        repository.getCompanies(it).asLiveData()
    }

}
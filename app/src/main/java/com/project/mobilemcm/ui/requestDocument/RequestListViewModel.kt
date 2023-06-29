package com.project.mobilemcm.ui.requestDocument

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.project.mobilemcm.data.Repository
import com.project.mobilemcm.data.local.database.model.RequestDocument
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestListViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    val docList = repository.getAllRequestDoc().asLiveData()


    fun insertDoc(requestDocument: RequestDocument) {
        viewModelScope.launch {
            repository.addRequestDoc(requestDocument)
        }
    }
}
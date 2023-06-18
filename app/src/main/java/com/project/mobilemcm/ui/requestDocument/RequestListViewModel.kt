package com.project.mobilemcm.ui.requestDocument

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.mobilemcm.data.Repository
import com.project.mobilemcm.data.local.database.model.RequestDocument
import com.project.mobilemcm.data.local.database.model.RequestDocumentItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestListViewModel @Inject constructor(
    private val repository: Repository
):ViewModel(){

    private val _docList=MutableLiveData<List<RequestDocumentItem>>()
    val docList=_docList

    init {
        viewModelScope.launch {
            _docList.postValue(repository.getAllRequestDoc())
        }
    }

    fun insertDoc(requestDocument: RequestDocument){
        viewModelScope.launch {
            repository.addRequestDoc(requestDocument)
        }
    }
}
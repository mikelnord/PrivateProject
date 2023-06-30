package com.project.mobilemcm.ui.requestDocument

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.project.mobilemcm.data.Repository
import com.project.mobilemcm.data.local.database.model.Good1c
import com.project.mobilemcm.data.local.database.model.RequestDocument
import com.project.mobilemcm.data.local.database.model.RequestDocumentToRequestDocument1c
import com.project.mobilemcm.data.login.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestListViewModel @Inject constructor(
    private val repository: Repository,
    private val loginRepository: LoginRepository
) : ViewModel() {

    val docList = repository.getAllRequestDoc().asLiveData()

    fun insertDoc(requestDocument: RequestDocument) {
        viewModelScope.launch {
            repository.addRequestDoc(requestDocument)
        }
    }

    fun sendDocument() {
        val listDoc = viewModelScope.async { repository.getAllDocumentForSend() }
        viewModelScope.launch {
            val activeUserId = loginRepository.getActiveUser()?.id
            listDoc.await().let { listDoc ->
                listDoc.forEach { doc ->
                    val listGoodsDoc = repository.goodsDoc(doc.document_id)
                    val itemList: MutableList<Good1c> = mutableListOf()
                    listGoodsDoc.forEach { requestGood ->
                        itemList.add(
                            Good1c(
                                id = requestGood.id,
                                price = if (((requestGood.priceInd
                                        ?: 0.0).compareTo(0.0) == 0) or (requestGood.priceInd == null)
                                ) requestGood.price else requestGood.priceInd,
                                count = requestGood.count,
                                metod = if ((requestGood.metod == 4) or (requestGood.metod == null)) 0 else requestGood.metod,
                                discount = requestGood.discont ?: 0.0
                            )
                        )
                    }
                    activeUserId?.let { userId ->
                        val doc1C = doc.RequestDocumentToRequestDocument1c(
                            userId,
                            itemList.toList()
                        )
                        val res = repository.postDoc(doc1C)
                        res.data?.let {
                            repository.sendDocumentUpdate(
                                it.id ?: "",
                                it.number ?: "",
                                doc1C.id_doc.toInt()
                            )
                        }
                    }
                }
            }
        }
    }


}
package com.project.mobilemcm.ui.reports

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.mobilemcm.data.Repository
import com.project.mobilemcm.data.local.database.model.PaymentsItem
import com.project.mobilemcm.data.local.database.model.Result
import com.project.mobilemcm.data.login.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PaymentsViewModel @Inject constructor(
    private val repository: Repository,
    private val loginRepository: LoginRepository
) : ViewModel() {

    init {
        getPayments()
    }

    private var _showDetail = MutableLiveData<Boolean>()
    val showDetail = _showDetail

    var paymentsItem: PaymentsItem? = null

    fun setShowDetail() {
        _showDetail.value = false
    }

    private var _paymentsList = MutableLiveData<List<PaymentsItem>?>()
    val paymentsList = _paymentsList

    private fun getPayments() {
        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.user?.let {
                val result = repository.getPayments(it.id)
                result.let { res ->
                    if (res.status == Result.Status.SUCCESS) {
                        _paymentsList.postValue(result.data?.payments)
                    }
                }
            }
        }
    }

    fun onItemClick(paymentsItem: PaymentsItem) {
        this.paymentsItem = paymentsItem
        _showDetail.value = true
    }

}
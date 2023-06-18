package com.project.mobilemcm.ui.exchange

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.mobilemcm.data.Repository
import com.project.mobilemcm.data.local.database.model.Result
import com.project.mobilemcm.data.login.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.system.measureTimeMillis

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val repository: Repository,
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _loadFile = MutableLiveData<Int>()
    val loadFile = _loadFile

    private val _message = MutableLiveData<String>()
    val message = _message

    private val _countGoods = MutableLiveData<Int>()
    val countGoods = _countGoods

    private val _timeSecGoods = MutableLiveData<Long>()
    val timeSecGoods = _timeSecGoods

    private fun setLoadProgress(progress: Int) {
        _loadFile.postValue(loadFile.value?.plus(progress) ?: 0)
    }

    var isError = false

    fun getObmen() {
        val strPodr = loginRepository.user?.division_id//stock
        val strUserId = loginRepository.user?.id ?: ""
        val fileObmen = viewModelScope.async(Dispatchers.IO) {
            val result =
                strPodr?.let { repository.fetchObmenFile("20010101", it, strUserId) }
            result?.let { res ->
                if (res.status == Result.Status.SUCCESS) {
                    isError = false
                    return@async result.data
                } else {
                    isError = true
                    message.postValue(result.message)
                    return@async null
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            timeSecGoods.postValue(measureTimeMillis {
                fileObmen.await()?.let {
                    setLoadProgress(repository.addGoodToBase(it))
                    _countGoods.postValue(loadFile.value)
                    setLoadProgress(repository.addCategoryToBase(it))
                    setLoadProgress(repository.addPricegroupToBase(it))
                    setLoadProgress(repository.addPricegroups2ToBase(it))
                    setLoadProgress(repository.addStoresToBase(it))
                    setLoadProgress(repository.addStockToBase(it))
                    setLoadProgress(repository.addCounterpartiesToBase(it))
                    setLoadProgress(repository.addCounterpartiesStoresToBase(it))
                    setLoadProgress(repository.addDiscontsToBase(it))
                    setLoadProgress(repository.addActionPricesToBase(it))
                    setLoadProgress(repository.addIndividualPricesToBase(it))
                    setLoadProgress(repository.addDivisionToBase(it))
                }
            } / 1000)
        }
    }

    fun insertVendors() {
        viewModelScope.launch {
            if (repository.getCountVendors())
                repository.addVendors(repository.getAllVendors())
        }
    }
}
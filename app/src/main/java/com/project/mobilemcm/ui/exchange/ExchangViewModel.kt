package com.project.mobilemcm.ui.exchange

import android.util.Log
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

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val repository: Repository,
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _loadFile = MutableLiveData<Int>()
    private val loadFile = _loadFile

    private val _message = MutableLiveData<String>()
    val message = _message

    private val _countGoods = MutableLiveData<Int>()
    val countGoods = _countGoods

    private val _dateObmen = MutableLiveData<String>()
    val dateObmen = _dateObmen

    private val _complateObmen = MutableLiveData<Boolean>()
    val complateObmen = _complateObmen

    var isError = false

    fun getObmen() {
        //2023-06-19
        //20010101
        val strPodr = loginRepository.user?.division_id//stock
        val strUserId = loginRepository.user?.id ?: ""
        val dateObmen = viewModelScope.async { repository.getObmenDate() }
        val fileObmen = viewModelScope.async(Dispatchers.IO) {
            _dateObmen.postValue(dateObmen.await()?.dateObmen ?: "20010101")
            val result =
                strPodr?.let {
                    repository.fetchObmenFile(
                        dateObmen.await()?.dateObmen ?: "20010101",
                        it,
                        strUserId
                    )
                }
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
            _complateObmen.postValue(false)
            fileObmen.await()?.let {
                try {
                    _dateObmen.postValue(fileObmen.await()?.goods?.size.toString())
                    repository.addGoodToBase(it)
                    _countGoods.postValue(loadFile.value)
                    repository.addCategoryToBase(it)
                    repository.addPricegroupToBase(it)
                    repository.addPricegroups2ToBase(it)
                    repository.addStoresToBase(it)
                    repository.addStockToBase(it)
                    repository.addCounterpartiesToBase(it)
                    repository.addCounterpartiesStoresToBase(it)
                    repository.addDateObmenToBase(it)
                    repository.addDiscontsToBase(it)
                    repository.addActionPricesToBase(it)
                    repository.addIndividualPricesToBase(it)
                    repository.addDivisionToBase(it)
                } catch (e: Throwable) {
                    Log.e("errorObmen", e.message.toString())
                }
            }
            _complateObmen.postValue(true)
        }
    }

    fun insertVendors() {
        viewModelScope.launch {
            if (repository.getCountVendors())
                repository.addVendors(repository.getAllVendors())
        }
    }
}
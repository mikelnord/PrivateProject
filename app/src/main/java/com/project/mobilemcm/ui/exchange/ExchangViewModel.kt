package com.project.mobilemcm.ui.exchange

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.project.mobilemcm.data.Repository
import com.project.mobilemcm.data.local.database.model.Result
import com.project.mobilemcm.data.login.LoginRepository
import com.project.mobilemcm.workers.ExchangeWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
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

    private val _firstObmen = MutableLiveData<String>()
    val firstObmen = _firstObmen

    private val _dateObmen = MutableLiveData<String>()
    val dateObmen = _dateObmen

    private val _complateObmen = MutableLiveData<Boolean>()
    val complateObmen = _complateObmen

    var isError = false

    fun getObmen(context: Context) {
        val strPodr = loginRepository.user?.division_id//stock
        val strUserId = loginRepository.user?.id ?: ""
        val dateObmen = viewModelScope.async { repository.getObmenDate() }
        val fileObmen = viewModelScope.async(Dispatchers.IO) {
            val result =
                strPodr?.let {
                    repository.fetchObmenFile(
                        dateObmen.await()?.dateObmen ?: "2001-01-01T00:00:00",
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
                    repository.addGoodToBase(it)
                    _countGoods.postValue(loadFile.value)
                    repository.addCategoryToBase(it)
                    repository.addPricegroupToBase(it)
                    repository.addPricegroups2ToBase(it)
                    repository.addStoresToBase(it)
                    repository.addStockToBase(it)
                    repository.addCounterpartiesToBase(it)
                    repository.addCounterpartiesStoresToBase(it)
                    repository.addDiscontsToBase(it)
                    repository.addActionPricesToBase(it)
                    repository.addIndividualPricesToBase(it)
                    repository.addDivisionToBase(it)
                    repository.addDateObmenToBase(it)
                } catch (e: Throwable) {
                    Log.e("errorObmen", e.message.toString())
                }
            }
            _dateObmen.postValue(repository.getObmenDate()?.dateObmen)
            delay(5000)
            _complateObmen.postValue(true)
            applyExchangeWorker(context)
        }
    }

    fun insertVendors() {
        viewModelScope.launch {
            if (repository.getCountVendors())
                repository.addVendors(repository.getAllVendors())
        }
    }

    private fun applyExchangeWorker(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<ExchangeWorker>(30, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "sendDateFromBase",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

}
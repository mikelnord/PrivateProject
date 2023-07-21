package com.project.mobilemcm.ui.masterdoc

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.project.mobilemcm.data.Repository
import com.project.mobilemcm.data.local.database.model.CompanyInfo
import com.project.mobilemcm.data.local.database.model.RequestDocument
import com.project.mobilemcm.data.local.database.model.Result
import com.project.mobilemcm.data.login.LoginRepository
import com.project.mobilemcm.pricing.logic.DiscountCompany
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AdapterViewModel @Inject constructor(
    private val repository: Repository,
    private val loginRepository: LoginRepository
) : ViewModel() {

    var requestDocument =
        RequestDocument(counterparties_id = "0", store_id = "c3a21002-ef22-11e5-a605-f07959941a7c")
    private var _queryCompanies = MutableLiveData<String>()
    private val queryCompanies = _queryCompanies

    private var _onlyMine = MutableLiveData(true)
    private val onlyMine = _onlyMine

    private var _companyInfo = MutableLiveData<CompanyInfo?>()
    val companyInfo = _companyInfo

    init {
        _queryCompanies.value = ""
    }

    fun setQueryCompanies(query: String) {
        _queryCompanies.value = query
    }

    fun setOnlyMine() {
        _onlyMine.value = !onlyMine.value!!
    }

    fun setOnlyMineStart() {
        _onlyMine.value = true
    }

    val listCompanies = queryCompanies.switchMap {
        when (onlyMine.value) {
            true -> loginRepository.user?.id?.let { id ->
                repository.getMyCompanies(it, id).asLiveData()
            }

            false -> repository.getCompanies(it).asLiveData()
            else -> {}
        }
        onlyMine.switchMap { only ->
            when (only) {
                true -> loginRepository.user?.id?.let { id ->
                    repository.getMyCompanies(it, id).asLiveData()
                }

                false -> repository.getCompanies(it).asLiveData()
            }
        }

    }

    val activeUser = liveData {
        emit(loginRepository.user)
    }

    val activeStore =
        liveData {
            loginRepository.user?.let {
                emit(repository.getDivisionByID(it.division_id))
            }
        }

    val storeList = liveData {
        loginRepository.user?.division_id?.let {
            emit(repository.getStoresFromDivision(it))
        }
    }

    fun getItemFromListStore(position: Int) = storeList.value?.get(position)?.id

    fun getPositionFromIdStore(id: String): Int {
        storeList.value?.forEachIndexed { index, storeItem ->
            if (storeItem.id == id) return index
        }
        return 0
    }

    private var _discountsCompany = MutableLiveData<List<DiscountCompany>?>()
    val discountsCompany = _discountsCompany


    fun getCompanyInfo(idCompany: String) {
        companyInfo.value = CompanyInfo(null, null, null)
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getCompanyInfo(idCompany)
            result.let { res ->
                if (res.status == Result.Status.SUCCESS) {
                    _companyInfo.postValue(result.data)
                }
            }
            launch {
                val listDiscount=repository.getDiscontsFromCompany(date = LocalDateTime.now().format(
                    DateTimeFormatter.ISO_DATE_TIME), company_id = idCompany)
                _discountsCompany.postValue(listDiscount)
            }
        }
    }

}
package com.project.mobilemcm.ui.categorylist

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.project.mobilemcm.data.Repository
import com.project.mobilemcm.data.local.database.model.Counterparties
import com.project.mobilemcm.data.local.database.model.CounterpartiesStores
import com.project.mobilemcm.data.local.database.model.DomainCategory
import com.project.mobilemcm.data.local.database.model.DomainCategoryChild
import com.project.mobilemcm.data.local.database.model.FilterState
import com.project.mobilemcm.data.local.database.model.Good1c
import com.project.mobilemcm.data.local.database.model.GoodWithStock
import com.project.mobilemcm.data.local.database.model.LoggedInUser
import com.project.mobilemcm.data.local.database.model.ParentCategory
import com.project.mobilemcm.data.local.database.model.Pricegroup
import com.project.mobilemcm.data.local.database.model.RequestDocument
import com.project.mobilemcm.data.local.database.model.RequestDocument1c
import com.project.mobilemcm.data.local.database.model.RequestGoods
import com.project.mobilemcm.data.local.database.model.Vendors
import com.project.mobilemcm.data.login.LoginRepository
import com.project.mobilemcm.pricing.data.IndividualPricesDao
import com.project.mobilemcm.pricing.logic.IndPrices
import com.project.mobilemcm.util.sanitizeSearchQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.collections.set

@HiltViewModel
class CategoryViewModel @Inject constructor(//rename to main viewmodel
    private val repository: Repository,
    private val individualPricesDao: IndividualPricesDao,
    private val loginRepository: LoginRepository
) : ViewModel() {


    private var _query = MutableLiveData<String>()
    val query = _query

    fun setQuery(query: String) {
        _query.value = query
    }

    private var _queryPricegroup = MutableLiveData<String>()
    private val queryPricegroup = _queryPricegroup

    fun setQueryPricegroup(query: String) {
        _queryPricegroup.value = query
    }

    private var _selectedPricegroup = MutableLiveData<Pricegroup>()
    val selectedPricegroup = _selectedPricegroup

    private fun setSelectedPricegroup(id: String) {
        viewModelScope.launch {
            _selectedPricegroup.postValue(repository.getPricegroup(id))
        }
    }

    val findPricegroup = queryPricegroup.switchMap {
        getAllPricegroup(it)
    }

    private fun getAllPricegroup(name: String) = repository.getAllPricegroup(name).asLiveData()

    private var _queryVendors = MutableLiveData<String>()
    private val queryVendors = _queryVendors

    fun setQueryVendors(query: String) {
        _queryVendors.value = query
    }

    private var _selectedVendors = MutableLiveData<Vendors>()
    val selectedVendors = _selectedVendors

    private fun setSelectedVendors(vendors: Vendors) {
        _selectedVendors.value = vendors
    }

    val findVendors = queryVendors.switchMap {
        repository.getVendors(it).asLiveData()
    }

    fun setText(id: String) {
        setStatePricegroup()
        setSelectedPricegroup(id)
    }

    fun setVendor(vendors: Vendors) {
        setStateVendor()
        setSelectedVendors(vendors)
    }

    private val _currentCategory = MutableLiveData<String>()
    private val currentCategory = _currentCategory

    val addStringsList: MutableMap<String, GoodWithStock> = mutableMapOf()

    private val _countList = MutableLiveData<Int>()
    val countList = _countList

    private var _showCategoryList = MutableLiveData<Boolean>()
    val showCategoryList = _showCategoryList

    fun setCategoryList() {
        _showCategoryList.value = false
    }

    fun showCategoryList() {
        _showCategoryList.value = true
    }

    private var _hideCategoryList = MutableLiveData<Boolean>()
    val hideCategoryList = _hideCategoryList

    fun setHideCategoryList() {
        _hideCategoryList.value = false
    }

    fun hideCategoryList() {
        _hideCategoryList.value = true
    }

    var requestDocument =
        RequestDocument(counterparties_id = "0", store_id = "")

    private fun sumValue() {
        _countList.value = addStringsList.values.count()
    }

    val docSumm = countList.switchMap {
        var sum = 0.0
        addStringsList.map {
            when (it.value.metod) {
                0 -> {
                    it.value.price?.let { price ->
                        sum += price * it.value.count
                    }
                }

                1 -> {
                    it.value.priceInd?.let { price ->
                        sum += price * it.value.count
                    }
                }

                2, 3 -> {
                    it.value.priceInd?.let { price ->
                        sum += price * it.value.count
                    }
                }

                4 -> {
                    it.value.discont?.let { discont ->
                        it.value.price?.let { price ->
                            sum += (it.value.count * price) - it.value.count * price / 100 * discont
                        }
                    }

                }

                else -> {
                    it.value.price?.let { price ->
                        sum += price * it.value.count
                    }
                }
            }
        }
        liveData { emit(sum) }
    }

    fun setStoreId(storeId: String) {
        requestDocument.store_id = storeId
    }

    val docCount = countList.switchMap {
        liveData { emit(addStringsList.values.sumOf { it.count }) }
    }

    init {
        sumValue()
    }

    fun addList(goodWithStock: GoodWithStock, count: Double) {
        if (addStringsList.contains(goodWithStock.id)) {
            addStringsList[goodWithStock.id]?.let {
                it.count += count
            }
        } else {
            goodWithStock.count = count
            addStringsList[goodWithStock.id] = goodWithStock
        }
        sumValue()
    }

    fun minList(goodWithStock: GoodWithStock, count: Double) {
        if (addStringsList.contains(goodWithStock.id)) {
            addStringsList[goodWithStock.id]?.let {
                if (count >= it.count) {
                    goodWithStock.count = 0.0
                    addStringsList.remove(goodWithStock.id)
                } else it.count -= count
            }
        }
        sumValue()
    }

    fun deleteFromList(goodWithStock: GoodWithStock) {
        if (addStringsList.contains(goodWithStock.id)) {
            addStringsList.remove(goodWithStock.id)
            goodWithStock.count = 0.0
        }
        sumValue()
    }

    fun inList(goodWithStock: GoodWithStock) = addStringsList.contains(goodWithStock.id)

    fun setCurrentCategory(id: String?) {
        id?.let {
            _currentCategory.value = it
        }
    }

    private var _isStateFilter = MutableLiveData(FilterState())
    val isStateFilter = _isStateFilter

    fun clearStateFilter() {
        _isStateFilter.value = FilterState()
    }

    fun setStateRemainder() {
        _isStateFilter.value = FilterState(
            !(_isStateFilter.value?.isRemainder ?: false),
            _isStateFilter.value?.isPrisegroup ?: false,
            _isStateFilter.value?.isVendor ?: false
        )
    }

    fun setStatePricegroup() {
        _isStateFilter.value = FilterState(
            _isStateFilter.value?.isRemainder ?: false,
            !(_isStateFilter.value?.isPrisegroup ?: false),
            _isStateFilter.value?.isVendor ?: false
        )
    }

    fun setStateVendor() {
        _isStateFilter.value = FilterState(
            _isStateFilter.value?.isRemainder ?: false,
            _isStateFilter.value?.isPrisegroup ?: false,
            !(_isStateFilter.value?.isVendor ?: false)
        )
    }

    private var activeUser: LoggedInUser? = null


    private fun getActiveUser() {
        viewModelScope.launch {
            activeUser = loginRepository.getActiveUser()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val categoryChildList = _isStateFilter.switchMap { isStateFilter ->
        repository.categoryBaseList(requestDocument.store_id).mapLatest { list ->
            var newList = if (isStateFilter.isRemainder) {
                list.filter { domainCategory ->
                    if (domainCategory.amount == null) false
                    else domainCategory.amount > 0
                }
            } else list

            newList = if (isStateFilter.isPrisegroup) {
                newList.filter { domainCategory ->
                    domainCategory.pricegroup == (selectedPricegroup.value?.id ?: "")
                }
            } else newList

            newList = if (isStateFilter.isVendor) {
                newList.filter { domainCategory ->
                    domainCategory.vendor == (selectedVendors.value?.name ?: "")
                }
            } else newList

            newList.map { ParentCategory(it.parent_id, it.name, it.code) }.distinct()
                .sortedBy { it.code }.map { parentCategory ->
                    val listChild = repository.categoryChildList(
                        parentCategory.parent_id,
                        requestDocument.store_id
                    )
                        .asSequence()
                        .filter { domainCategory ->
                            if (isStateFilter.isRemainder) {
                                if (domainCategory.amount == null) false
                                else domainCategory.amount > 0
                            } else true
                        }.filter { domainCategory ->
                            if (isStateFilter.isPrisegroup) {
                                domainCategory.pricegroup == (selectedPricegroup.value?.id ?: "")
                            } else true
                        }.filter { domainCategory ->
                            if (isStateFilter.isVendor) {
                                domainCategory.vendor == (selectedVendors.value?.name ?: "")
                            } else true
                        }.map { DomainCategory(it.id, it.parent_id, it.name, "", 0.0, "", "") }
                        .distinct()
                        .toList()
                    DomainCategoryChild(parentCategory.name, listChild)
                }
        }.asLiveData()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val listGood = _isStateFilter.switchMap { isStateFilter ->
        currentCategory.switchMap {
            repository.getChildGoods(it, requestDocument.store_id).mapLatest { list ->
                var newList = if (isStateFilter.isRemainder) {
                    list.filter { goodWithStock ->
                        goodWithStock.amount.let { amount ->
                            if (amount == null) false
                            else amount > 0
                        }
                    }
                } else list
                getActiveUser()
                newList.forEach { goodWithStock ->
                    val gp = getPricing(
                        division_id = loginRepository.user?.division_id ?: "",
                        good_id = goodWithStock.id,
                        company_id = selectedCompanies.value?.id ?: "",
                        date = "2023-06-27T08:00:00",
                        goodWithStock.pricegroup,
                        goodWithStock.pricegroup2,
                        selectedCompanies.value?.apply_actions ?: false
                    )
                    gp?.let { gp ->
                        goodWithStock.discont = gp.discount
                        goodWithStock.priceInd = gp.price
                        goodWithStock.number = gp.number
                        goodWithStock.metod =
                            if ((gp.discount.compareTo(0.0) == 0) and (gp.metod == 4)) 0 else gp.metod
                    }
                }

                newList = if (isStateFilter.isPrisegroup) {
                    newList.filter { goodWithStock ->
                        goodWithStock.pricegroup == (selectedPricegroup.value?.id ?: "")
                    }
                } else newList

                newList = if (isStateFilter.isVendor) {
                    newList.filter { goodWithStock ->
                        goodWithStock.vendors == (selectedVendors.value?.name ?: "")
                    }
                } else newList
                sync(newList)
            }.asLiveData()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val findGoods = _isStateFilter.switchMap { isStateFilter ->
        query.switchMap { query ->
            repository.getSearch(sanitizeSearchQuery(query), requestDocument.store_id)
                .onStart { _showProgress.value = true }
                .onEmpty { _showProgress.value = false }
                .onCompletion { _showProgress.value = false }
                .mapLatest { list ->
                    var newList = if (isStateFilter.isRemainder) {
                        list.filter { goodWithStock ->
                            goodWithStock.amount.let { amount ->
                                if (amount == null) false
                                else amount > 0
                            }
                        }
                    } else list
                    getActiveUser()
                    newList.forEach {
                        val gp = getPricing(
                            division_id = loginRepository.user?.division_id ?: "",
                            good_id = it.id,
                            company_id = selectedCompanies.value?.id ?: "",
                            date = "2023-06-27T08:00:00",
                            it.pricegroup,
                            it.pricegroup2,
                            selectedCompanies.value?.apply_actions ?: false
                        )
                        gp?.let { gp ->
                            it.discont = gp.discount
                            it.priceInd = gp.price
                            it.number = gp.number
                            it.metod =
                                if ((gp.discount.compareTo(0.0) == 0) and (gp.metod == 4)) 0 else gp.metod
                        }
                    }
                    newList = if (isStateFilter.isPrisegroup) {
                        newList.filter { goodWithStock ->
                            goodWithStock.pricegroup == (selectedPricegroup.value?.id ?: "")
                        }
                    } else newList

                    newList = if (isStateFilter.isVendor) {
                        newList.filter { goodWithStock ->
                            goodWithStock.vendors == (selectedVendors.value?.name ?: "")
                        }
                    } else newList
                    sync(newList)
                }.asLiveData()
        }
    }

    fun sync(list: List<GoodWithStock>) =
        list.map {
            if (addStringsList.contains(it.id))
                it.count = addStringsList[it.id]?.count ?: 0.0
            it
        }


    private var _showProgress = MutableLiveData<Boolean>()
    val showProgress = _showProgress


    fun saveDoc(): Boolean {
        if (requestDocument.counterparties_id.isEmpty()) {
            return false
        }
        docSumm.value?.let {
            requestDocument.summDoc = it
        }
        val idDoc = viewModelScope.async {
            repository.addRequestDoc(requestDocument)
        }
        viewModelScope.launch {
            val requestGoodsList: MutableList<RequestGoods> = mutableListOf()
            idDoc.await().let {
                repository.clearGoodsDocuments(it)
                addStringsList.map { itmap ->
                    requestGoodsList.add(
                        RequestGoods(
                            it,
                            itmap.key,
                            itmap.value.count,
                            itmap.value.price ?: 0.0,
                            itmap.value.number,
                            itmap.value.priceInd,
                            itmap.value.discont,
                            itmap.value.metod
                        )
                    )
                }
            }
            repository.insertGoodsDoc(requestGoodsList)
            val itemList: MutableList<Good1c> = mutableListOf()
            addStringsList.values.forEach { requestGood ->
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
            val date = LocalDateTime.ofInstant(
                requestDocument.docDate.toInstant(),
                requestDocument.docDate.timeZone.toZoneId()
            )
            val requestDocument1c = RequestDocument1c(
                userId = loginRepository.getActiveUser()?.id ?: "",
                id_doc = if (requestDocument.document_id.compareTo(0) == 0) repository.getLastDocid()
                    ?: 1 else requestDocument.document_id,
                docDate = date.format(DateTimeFormatter.ISO_DATE_TIME),
                store_id = requestDocument.store_id,
                counterparties_id = requestDocument.counterparties_id,
                counterpartiesStores_id = requestDocument.counterpartiesStores_id,
                isPickup = requestDocument.isPickup,
                itemList = itemList,
                comment = requestDocument.comment

            )
//            println(Gson().toJson(requestDocument1c))
            try {
                if (false) {
                    val res = repository.postDoc(requestDocument1c)
                    res.data?.let {
                        repository.sendDocumentUpdate(
                            it.id ?: "",
                            it.number ?: "",
                            requestDocument1c.id_doc.toInt()
                        )
                    }
                }
            } catch (e: Throwable) {
                Log.e("errorSendDocument", e.message.toString())
            }
            clearDoc()
        }
        return true
    }


    fun clearDoc() {
        requestDocument = RequestDocument(
            counterparties_id = "0",
            store_id = "c3a21002-ef22-11e5-a605-f07959941a7c"
        )
        addStringsList.clear()
        sumValue()
        _showProgress.value = false
        _selectedCompanies.value = Counterparties("")
        _selectedCompaniesAdr.value = CounterpartiesStores("", null, null, null, null, null, null)
    }

    fun openDoc(id: Long) {
        clearDoc()
        val result = viewModelScope.async {
            repository.getDoc(id)
        }
        val resultGoods = viewModelScope.async {
            repository.goodsDoc(id)
        }
        viewModelScope.launch {
            result.await().let {
                requestDocument = it
                _selectedCompanies.value = repository.getCompany(it.counterparties_id)
                repository.getAdresCompany(it.counterpartiesStores_id)?.let { adr ->
                    _selectedCompaniesAdr.value = adr
                }
            }
            resultGoods.await().let {
                it.map { goodWithStock ->
                    addList(goodWithStock, goodWithStock.count)//пройти и заполнить остатки
                    sumValue()
                }
                val mapStock = repository.getMapAmount(
                    requestDocument.store_id,
                    addStringsList.keys.toList()
                )
                addStringsList.map { map ->
                    if (mapStock.contains(map.key)) {
                        map.value.amount = mapStock[map.key] ?: 0.0
                    } else map.value.amount = 0.0
                }
                _goDoc.value = true
            }
        }

    }

    private var _goDoc = MutableLiveData(false)
    val goDoc = _goDoc

    fun setgoDoc() {
        _goDoc.value = false
    }

    private var _selectedCompanies = MutableLiveData<Counterparties>()
    val selectedCompanies = _selectedCompanies

    fun setSelectedCompanies(counterparties: Counterparties) {
        _selectedCompanies.value = counterparties
    }

    private var _selectedCompaniesAdr = MutableLiveData<CounterpartiesStores>()
    val selectedCompaniesAdr = _selectedCompaniesAdr

    fun setSelectedCompaniesAdr(counterpartiesStores: CounterpartiesStores) {
        _selectedCompaniesAdr.value = counterpartiesStores
    }

    private var _queryCompaniesAdr = MutableLiveData<String?>()
    private val queryCompaniesAdr = _queryCompaniesAdr

    fun setQueryCompaniesAdr(query: String) {
        _queryCompaniesAdr.value = query
    }

    fun getCompanyAdres(): MediatorLiveData<List<CounterpartiesStores>> {
        val result = MediatorLiveData<List<CounterpartiesStores>>()
        result.addSource(queryCompaniesAdr) { value ->
            viewModelScope.launch {
                result.value = selectedCompanies.value?.let {
                    repository.getAdressFromCompany(
                        it.id,
                        value.toString()
                    )
                }
            }
        }
        result.addSource(selectedCompanies) { value ->
            viewModelScope.launch {
                result.value =
                    repository.getAdressFromCompany(value.id, queryCompaniesAdr.value ?: "")
            }
        }
        return result
    }


    private suspend fun getPricing(
        division_id: String, good_id: String, company_id: String, date: String, pricegroup: String,
        pricegroup2: String, apply_actions: Boolean
    ): IndPrices? {
        var result = individualPricesDao.getIndividualPices(good_id, company_id, date)
        if (result == null) {
            //individual prices not allowed
            val im = individualPricesDao.getIm(company_id)
            if (apply_actions) {
                im?.let {
                    result =
                        if (it) individualPricesDao.getActionPricesIm(division_id, good_id, date)
                        else individualPricesDao.getActionPricesOther(division_id, good_id, date)
                }
            }
            if (result == null) {
                result = individualPricesDao.getDisconts(
                    company_id = company_id, date = date, pricegroup = pricegroup,
                    pricegroup2 = pricegroup2, good_id = good_id
                )
                if (result == null) {
                    //ничего не нашли базовая цена
                    return null
                } else {

                    return result
                }
            } else {
                //pricing action find and return
                return result
            }
        } else {
            //individual prices return
            return result
        }
    }

}
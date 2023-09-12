package com.project.mobilemcm.ui.categorylist

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.project.mobilemcm.BuildConfig
import com.project.mobilemcm.data.Repository
import com.project.mobilemcm.data.local.database.model.CompanyInfo
import com.project.mobilemcm.data.local.database.model.Counterparties
import com.project.mobilemcm.data.local.database.model.CounterpartiesStores
import com.project.mobilemcm.data.local.database.model.DomainCategory
import com.project.mobilemcm.data.local.database.model.DomainCategoryChild
import com.project.mobilemcm.data.local.database.model.FilterState
import com.project.mobilemcm.data.local.database.model.Good1c
import com.project.mobilemcm.data.local.database.model.GoodWithStock
import com.project.mobilemcm.data.local.database.model.ParentCategory
import com.project.mobilemcm.data.local.database.model.Pricegroup
import com.project.mobilemcm.data.local.database.model.RequestDocument
import com.project.mobilemcm.data.local.database.model.RequestDocumentToRequestDocument1c
import com.project.mobilemcm.data.local.database.model.RequestGoods
import com.project.mobilemcm.data.local.database.model.Result
import com.project.mobilemcm.data.local.database.model.SummDoc
import com.project.mobilemcm.data.local.database.model.Vendors
import com.project.mobilemcm.data.login.LoginRepository
import com.project.mobilemcm.pricing.data.IndividualPricesDao
import com.project.mobilemcm.pricing.logic.DiscountCompany
import com.project.mobilemcm.pricing.logic.IndPrices
import com.project.mobilemcm.util.sanitizeSearchQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
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

    var firstLaunchPodbor = true

    fun setLaunchPodbor(bool: Boolean = false) {
        firstLaunchPodbor = bool
    }

    private var _companyInfo = MutableLiveData<CompanyInfo?>()
    val companyInfo = _companyInfo

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
                val listDiscount = repository.getDiscontsFromCompany(
                    date = LocalDateTime.now().format(
                        DateTimeFormatter.ISO_DATE_TIME
                    ), company_id = idCompany
                )
                _discountsCompany.postValue(listDiscount)
            }
        }
    }


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

    private var _selectedPricegroup = MutableLiveData<Pricegroup?>()
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

    private var _selectedVendors = MutableLiveData<Vendors?>()
    val selectedVendors = _selectedVendors

    private var _clearAll = MutableLiveData(false)
    val clearAll = _clearAll

    fun setClearAll(boolean: Boolean) {
        _clearAll.value = boolean
    }

    private fun setSelectedVendors(vendors: Vendors) {
        _selectedVendors.value = vendors
    }

    fun clearSelectedVendors() {
        _selectedVendors.value = null
    }

    fun clearSelectedPriceGroup() {
        _selectedPricegroup.value = null
    }

    private fun clearSelectedCategory() {
        _currentCategory.value = null
        _currentCategoryName.value = null
    }

    val findVendors = queryVendors.switchMap {
        repository.getVendors(it).asLiveData()
    }


    fun setText(id: String) {
        _isStateFilter.value = FilterState(
            _isStateFilter.value?.isRemainder ?: false,
            true,
            _isStateFilter.value?.isVendor ?: false
        )
        setSelectedPricegroup(id)
        setClearAll(true)
    }

    fun setVendor(vendors: Vendors) {
        _isStateFilter.value = FilterState(
            _isStateFilter.value?.isRemainder ?: false,
            _isStateFilter.value?.isPrisegroup ?: false,
            true
        )
        setSelectedVendors(vendors)
        setClearAll(true)
    }

    fun setRemainder(boolean: Boolean) {
        _isStateFilter.value = FilterState(
            boolean,
            _isStateFilter.value?.isPrisegroup ?: false,
            _isStateFilter.value?.isVendor ?: false
        )
    }

    fun getActiveUser() = liveData {
        emit(loginRepository.user)
        loginRepository.user?.let { getDefaultStorage(it.division_id) }
    }


    private fun getDefaultStorage(divisionId: String) {
        if (requestDocument.store_id.isEmpty()) {
            viewModelScope.launch {
                requestDocument.store_id = repository.getStoreDefault(divisionId)?.id ?: ""
            }
        }
    }

    private val _currentCategory = MutableLiveData<String?>()
    val currentCategory = _currentCategory

    private val _currentCategoryName = MutableLiveData<String?>()
    val currentCategoryName = _currentCategoryName

    val addStringsList: MutableMap<String, GoodWithStock> = mutableMapOf()

    private val _countList = MutableLiveData<Int>()
    val countList = _countList

    var requestDocument =
        RequestDocument(counterparties_id = "0", store_id = "")

    private fun sumValue() {
        _countList.value = addStringsList.values.count()
    }

    val docSumm = countList.switchMap {
        var sum = 0.0
        var sumFull = 0.0
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
            it.value.price?.let { price ->
                sumFull += price * it.value.count
            }
        }
        liveData { emit(SummDoc(sum, sumFull - sum)) }
    }


    fun setStoreId(storeId: String) {
        requestDocument.store_id = storeId
    }

    val docCount = countList.switchMap {
        liveData { emit(addStringsList.values.sumOf { it.count }) }
    }

    init {
        sumValue()
        updateAvailable()
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

    fun setCurrentCategory(id: String?, name: String) {
        id?.let {
            _currentCategory.value = it
            _currentCategoryName.value = name
            setClearAll(true)
        }
    }

    private var _isStateFilter = MutableLiveData(FilterState())
    val isStateFilter = _isStateFilter

    fun clearStateFilter() {
        _isStateFilter.value = FilterState()
        clearSelectedVendors()
        clearSelectedPriceGroup()
        clearSelectedCategory()
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val categoryChildList = _isStateFilter.switchMap { isStateFilter ->
        repository.categoryBaseList(requestDocument.store_id).mapLatest { list ->
            val newList = if (isStateFilter.isRemainder) {
                list.filter { domainCategory ->
                    if (domainCategory.amount == null) false
                    else domainCategory.amount > 0
                }
            } else list

            newList.map { ParentCategory(it.parent_id, it.name, it.code) }.distinct()
                .sortedBy { it.code }.map { parentCategory ->
                    val listChild = repository.categoryChildList(
                        parentCategory.parent_id,
                        requestDocument.store_id
                    )
                        .asSequence()
                        .map { DomainCategory(it.id, it.parent_id, it.name, "", 0.0, "", "") }
                        .distinct()
                        .toList()
                    DomainCategoryChild(parentCategory.name, listChild)
                }
        }.asLiveData()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val listGood = _isStateFilter.switchMap { isStateFilter ->
        currentCategory.switchMap { currentCategory ->
            if (!currentCategory.isNullOrEmpty()) {
                repository.getChildGoods(currentCategory, requestDocument.store_id)
                    .mapLatest { list ->
                        var newList = if (isStateFilter.isRemainder) {
                            list.filter { goodWithStock ->
                                if (goodWithStock.amount == null || goodWithStock.price == null) false
                                else (goodWithStock.amount!! > 0 && goodWithStock.price != 0.0)
                            }
                        } else list
                        newList.forEach { goodWithStock ->
                            val gp = getPricing(
                                divisionId = loginRepository.user?.division_id ?: "",
                                goodId = goodWithStock.id,
                                companyId = selectedCompanies.value?.id ?: "",
                                date = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                                goodWithStock.pricegroup,
                                goodWithStock.pricegroup2,
                                selectedCompanies.value?.apply_actions ?: false
                            )
                            gp?.let { indPrices ->
                                goodWithStock.discont = indPrices.discount
                                goodWithStock.priceInd = indPrices.price
                                goodWithStock.number = indPrices.number
                                goodWithStock.metod =
                                    if ((indPrices.discount.compareTo(0.0) == 0) and (indPrices.metod == 4)) 0 else indPrices.metod
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
            } else {
                liveData { emit(listOf()) }
            }
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
                            if (goodWithStock.amount == null || goodWithStock.price == null) false
                            else (goodWithStock.amount!! > 0 && goodWithStock.price != 0.0)
                        }
                    } else list
                    newList.forEach {
                        val gp = getPricing(
                            divisionId = loginRepository.user?.division_id ?: "",
                            goodId = it.id,
                            companyId = selectedCompanies.value?.id ?: "",
                            date = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                            it.pricegroup,
                            it.pricegroup2,
                            selectedCompanies.value?.apply_actions ?: false
                        )
                        gp?.let { indPrices ->
                            it.discont = indPrices.discount
                            it.priceInd = indPrices.price
                            it.number = indPrices.number
                            it.metod =
                                if ((indPrices.discount.compareTo(0.0) == 0) and (indPrices.metod == 4)) 0 else indPrices.metod
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
                    _showProgress.value = false
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

    private suspend fun recalculationOfPrices() {
        if (addStringsList.values.isNotEmpty()) {
            addStringsList.values.forEach { goodWithStock ->
                val ip = getPricing(
                    divisionId = loginRepository.user?.division_id ?: "",
                    goodId = goodWithStock.id,
                    companyId = selectedCompanies.value?.id ?: "",
                    date = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                    goodWithStock.pricegroup,
                    goodWithStock.pricegroup2,
                    selectedCompanies.value?.apply_actions ?: false
                )
                ip?.let { gp ->
                    goodWithStock.discont = gp.discount
                    goodWithStock.priceInd = gp.price
                    goodWithStock.number = gp.number
                    goodWithStock.metod =
                        if ((gp.discount.compareTo(0.0) == 0) and (gp.metod == 4)) 0 else gp.metod
                }
                if (ip == null) {
                    goodWithStock.discont = null
                    goodWithStock.priceInd = null
                    goodWithStock.number = null
                    goodWithStock.metod = null
                }
            }
            _countList.value = 0
            sumValue()
        }
    }

    private var _showProgress = MutableLiveData<Boolean>()
    val showProgress = _showProgress

    fun saveDoc(): Boolean {
        if (requestDocument.counterparties_id.isEmpty()) {
            return false
        }
        if (requestDocument.contract_id.isEmpty()) {
            return false
        }
        if (requestDocument.isSent) {
            return false
        }
        setLaunchPodbor(true)

        docSumm.value?.let {
            requestDocument.summDoc = it.docSumm
        }

        if (requestDocument.contract_type == "Наличный юр. лицо (до 99 тыс руб)" && requestDocument.summDoc > 100000.0) {
           return false
        }

        val idDoc = viewModelScope.async {
            repository.addRequestDoc(requestDocument)
        }
        clearStateFilter()
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

            val requestDocument1c = requestDocument.RequestDocumentToRequestDocument1c(
                loginRepository.getActiveUser()?.id ?: "", itemList
            )
            println(Gson().toJson(requestDocument1c))
//            try {
//                if (!requestDocument.isSent) {
//                    val res = repository.postDoc(requestDocument1c)
//                    res.data?.let { answerServer ->
//                        answerServer.id?.let {
//                            repository.sendDocumentUpdate(
//                                it,
//                                answerServer.number ?: "",
//                            )
//                        }
//                    }
//                }
//            } catch (e: Throwable) {
//                Log.e("errorSendDocument", e.message.toString())
//            }
            clearDoc()
        }
        return true
    }

    fun clearDoc() {
        requestDocument = RequestDocument(
            counterparties_id = "0",
            store_id = ""
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
                    addList(goodWithStock, goodWithStock.count)
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
        requestDocument.contract_id = counterparties.default_contract
        requestDocument.counterparties_id = counterparties.id
        requestDocument.counterpartiesStores_id = ""
        requestDocument.isPickup = false
        _selectedCompanies.value = counterparties
        viewModelScope.launch {
            recalculationOfPrices()
        }
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
        divisionId: String, goodId: String, companyId: String, date: String, pricegroup: String,
        pricegroup2: String, applyActions: Boolean
    ): IndPrices? {
        var result = individualPricesDao.getIndividualPices(goodId, companyId, date)
        if (result == null) {
            //individual prices not allowed
            val im = individualPricesDao.getIm(companyId)
            if (applyActions) {
                im?.let {
                    result =
                        if (it) individualPricesDao.getActionPricesIm(divisionId, goodId, date)
                        else individualPricesDao.getActionPricesOther(divisionId, goodId, date)
                }
            }
            return if (result == null) {
                result = individualPricesDao.getDisconts(
                    company_id = companyId, date = date, pricegroup = pricegroup,
                    pricegroup2 = pricegroup2, good_id = goodId
                )
                if (result == null) {
                    //ничего не нашли базовая цена
                    null
                } else {
                    result
                }
            } else {
                //pricing action find and return
                result
            }
        } else {
            //individual prices return
            return result
        }
    }

    //val versionCode = BuildConfig.VERSION_CODE
    private val versionName = BuildConfig.VERSION_NAME

    private var _updateAvailable = MutableLiveData(false)
    val updateAvailable = _updateAvailable

    private fun updateAvailable() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                repository.getUpdateVersionInfo().data?.let {
                    _updateAvailable.postValue(it.version != versionName)
                }
                delay(300000)//worker need
            }
        }
    }


    var contractsCompanyList = selectedCompanies.switchMap {
        liveData { emit(repository.getCompanyContract(it.id)) }
    }

    fun getPositionFromIdContract(id: String): Int {
        contractsCompanyList.value?.forEachIndexed { index, contractItem ->
            if (contractItem.id == id) return index
        }
        return 0
    }

    fun getItemFromListContracts(position: Int) = contractsCompanyList.value?.get(position)

}
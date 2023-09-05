package com.project.mobilemcm.data

import com.project.mobilemcm.data.local.database.AppDatabase
import com.project.mobilemcm.data.local.database.CategoryDao
import com.project.mobilemcm.data.local.database.CounterpartiesDao
import com.project.mobilemcm.data.local.database.CounterpartiesStoresDao
import com.project.mobilemcm.data.local.database.DivisionDao
import com.project.mobilemcm.data.local.database.GoodDao
import com.project.mobilemcm.data.local.database.ObmenDateDao
import com.project.mobilemcm.data.local.database.PricegroupDao
import com.project.mobilemcm.data.local.database.Pricegroups2Dao
import com.project.mobilemcm.data.local.database.RequestDocumentDao
import com.project.mobilemcm.data.local.database.RequestGoodsDao
import com.project.mobilemcm.data.local.database.StockDao
import com.project.mobilemcm.data.local.database.StoreDao
import com.project.mobilemcm.data.local.database.VendorsDao
import com.project.mobilemcm.data.local.database.model.FileObmen
import com.project.mobilemcm.data.local.database.model.GoodWithStock
import com.project.mobilemcm.data.local.database.model.ObmenDate
import com.project.mobilemcm.data.local.database.model.RequestDocument
import com.project.mobilemcm.data.local.database.model.RequestDocument1c
import com.project.mobilemcm.data.local.database.model.RequestGoods
import com.project.mobilemcm.data.local.database.model.Result
import com.project.mobilemcm.data.local.database.model.Vendors
import com.project.mobilemcm.data.remote.RemoteDataSource
import com.project.mobilemcm.pricing.data.ActionPricesDao
import com.project.mobilemcm.pricing.data.CompanyDao
import com.project.mobilemcm.pricing.data.DiscontsDao
import com.project.mobilemcm.pricing.data.IndividualPricesDao
import com.project.mobilemcm.pricing.data.ItemActionDao
import com.project.mobilemcm.pricing.data.ItemDao
import com.project.mobilemcm.pricing.data.ItemIndDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val categoryDao: CategoryDao,
    private val goodDao: GoodDao,
    private val pricegroups2Dao: Pricegroups2Dao,
    private val pricegroupDao: PricegroupDao,
    private val storeDao: StoreDao,
    private val stockDao: StockDao,
    private val requestDocumentDao: RequestDocumentDao,
    private val counterpartiesDao: CounterpartiesDao,
    private val requestGoodsDao: RequestGoodsDao,
    private val vendorsDao: VendorsDao,
    private val counterpartiesStoresDao: CounterpartiesStoresDao,
    private val companyDao: CompanyDao,
    private val discontsDao: DiscontsDao,
    private val itemDao: ItemDao,
    private val actionPricesDao: ActionPricesDao,
    private val itemActionDao: ItemActionDao,
    private val individualPricesDao: IndividualPricesDao,
    private val itemIndDao: ItemIndDao,
    private val divisionDao: DivisionDao,
    private val obmenDateDao: ObmenDateDao,
    private val appDatabase: AppDatabase
) {

    suspend fun fetchObmenFile(
        strDate: String,
        strPodr: String,
        strUserId: String
    ): Result<FileObmen> {
        return remoteDataSource.startObmen(strDate, strPodr, strUserId)
    }

    suspend fun addGoodToBase(fileObmen: FileObmen): Int {
        fileObmen.goods?.let { goods ->
            goodDao.insertAll(goods)
            return goods.size
        }
        return 0
    }

    suspend fun addDateObmenToBase(fileObmen: FileObmen) {
        fileObmen.date.let { date ->
            obmenDateDao.insert(ObmenDate(dateObmen = date))
        }
    }

    suspend fun getObmenDate() = obmenDateDao.getDate()

    fun getFlowDate() = obmenDateDao.getFlowDate()

    suspend fun firstLogin() = obmenDateDao.getCountObmenDate()

    suspend fun addCategoryToBase(fileObmen: FileObmen): Int {
        fileObmen.categories?.let { categorys ->
            categoryDao.insertAll(categorys)
            return categorys.size
        }
        return 0
    }

    suspend fun addPricegroups2ToBase(fileObmen: FileObmen): Int {
        fileObmen.pricegroups2?.let { pricegroups2 ->
            pricegroups2Dao.insertAll(pricegroups2)
            return pricegroups2.size
        }
        return 0
    }

    suspend fun addPricegroupToBase(fileObmen: FileObmen): Int {
        fileObmen.pricegroups?.let { pricegroup ->
            pricegroupDao.insertAll(pricegroup)
            return pricegroup.size
        }
        return 0
    }

    suspend fun addStoresToBase(fileObmen: FileObmen): Int {
        fileObmen.store?.let { stores ->
            storeDao.insertAll(stores)
            return stores.size
        }
        return 0
    }

    suspend fun addStockToBase(fileObmen: FileObmen): Int {
        fileObmen.stocks?.let { stocks ->
            stockDao.insertAll(stocks)
            return stocks.size
        }
        return 0
    }

    suspend fun addCounterpartiesToBase(fileObmen: FileObmen): Int {
        fileObmen.companies?.let { counterpartiesList ->
            counterpartiesDao.insertAll(counterpartiesList)
            return counterpartiesList.size
        }
        return 0
    }

    suspend fun addCounterpartiesStoresToBase(fileObmen: FileObmen): Int {
        fileObmen.company_stores?.let { counterpartiesStoresList ->
            counterpartiesStoresDao.insertAll(counterpartiesStoresList)
            return counterpartiesStoresList.size
        }
        return 0
    }

    suspend fun addDiscontsToBase(fileObmen: FileObmen): Int {
        fileObmen.discounts?.let { discontsList ->
            discontsList.forEach {
                it.items?.let { items -> itemDao.insertAll(items) }
                it.companies?.let { companies -> companyDao.insertAll(companies) }
                it.items = listOf()
                it.companies = listOf()
            }
            discontsDao.insertAll(discontsList)
            return discontsList.size
        }
        return 0
    }

    suspend fun addActionPricesToBase(fileObmen: FileObmen): Int {
        fileObmen.action_prices?.let { actionList ->
            actionList.forEach {
                if (!it.items.isNullOrEmpty()) {
                    itemActionDao.insertAll(it.items)
                    it.items = listOf()
                }
            }
            actionPricesDao.insertAll(actionList)
            return actionList.size
        }
        return 0
    }

    suspend fun addIndividualPricesToBase(fileObmen: FileObmen): Int {
        fileObmen.individual_prices?.let { individualPricesList ->
            individualPricesList.forEach {
                itemIndDao.insertAll(it.items)
                it.items = listOf()
            }
            individualPricesDao.insertAll(individualPricesList)
            return individualPricesList.size
        }
        return 0
    }

    suspend fun addDivisionToBase(fileObmen: FileObmen): Int {
        fileObmen.divisions?.let { divisionsList ->
            divisionDao.insertAll(divisionsList)
            return divisionsList.size
        }
        return 0
    }

    fun getChildGoods(id: String, store: String) = goodDao.getChildGoods(id, store)

    fun getSearch(query: String, store: String): Flow<List<GoodWithStock>> =
        if (query.trim().isEmpty()) goodDao.fullList(store)
        else goodDao.search(query, store)


    suspend fun addRequestDoc(requestDocument: RequestDocument): Long =
        requestDocumentDao.insert(requestDocument)

    fun getAllRequestDoc() = requestDocumentDao.getAll()

    suspend fun insertGoodsDoc(requestGoodsList: List<RequestGoods>) =
        requestGoodsDao.insertAll(requestGoodsList)

    suspend fun getDoc(id: Long) = requestDocumentDao.getDocument(id)

    suspend fun goodsDoc(idDoc: Long) = requestGoodsDao.goodsDoc(idDoc)

    fun getAllPricegroup(name: String) = pricegroupDao.getAll(name)

    suspend fun getPricegroup(id: String) = pricegroupDao.getPricegroup(id)

    fun categoryBaseList(store: String) = categoryDao.categoryBaseList(store)

    suspend fun categoryChildList(parent: String, store: String) =
        categoryDao.categoryChildList(parent, store)

    suspend fun addVendors(vendors: List<Vendors>) = vendorsDao.insertAll(vendors)

    fun getVendors(name: String) = vendorsDao.getVendors(name)

    suspend fun getAllVendors() = goodDao.allVendors()

    suspend fun getCountVendors() = vendorsDao.getCountVendors() == 0

    fun getCompanies(name: String) = counterpartiesDao.getCompanies(name)

    fun getMyCompanies(name: String, id: String) =
        counterpartiesDao.getMyCompanies(name, id)

    suspend fun getCompany(id: String) = counterpartiesDao.getCompany(id)

    suspend fun getMapAmount(store_id: String, listId: List<String>) =
        stockDao.getMapAmount(store_id = store_id, list_id = listId)

    suspend fun getAdressFromCompany(idCompany: String, name: String) =
        counterpartiesStoresDao.getAdressFromCompany(idCompany, name)

    suspend fun getAdresCompany(idAdr: String) = counterpartiesStoresDao.getAdresCompany(idAdr)

    suspend fun clearGoodsDocuments(idDoc: Long) = requestGoodsDao.clearGoodsDocuments(idDoc)

    fun clearBase() = appDatabase.clearAllTables()

    suspend fun getDivisionByID(id: String) = divisionDao.getDivisionByID(id)

    suspend fun getLastDocid() = individualPricesDao.getLastDocid()

    suspend fun getStoresFromDivision(divisionId: String) =
        storeDao.getStoresFromDivision(divisionId)

    suspend fun postDoc(requestDocument1c: RequestDocument1c) =
        remoteDataSource.postDoc(requestDocument1c)

    suspend fun sendDocumentUpdate(idOneC: String, number: String) =
        requestDocumentDao.sendDocumentUpdate(idOneC, number)

    suspend fun getAllDocumentForSend() = requestDocumentDao.getAllDocumentForSend()

    suspend fun getCompanyInfo(companyId: String) = remoteDataSource.getCompanyInfo(companyId)

    suspend fun getUpdateVersionInfo() = remoteDataSource.getVersionInfo()

    suspend fun getDiscontsFromCompany(company_id: String, date: String) =
        discontsDao.getDiscontsFromCompany(company_id, date)

    suspend fun getStoreDefault(divisionId: String) = storeDao.getStoreDefault(divisionId)

}

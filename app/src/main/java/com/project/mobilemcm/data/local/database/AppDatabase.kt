package com.project.mobilemcm.data.local.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import com.project.mobilemcm.data.local.database.model.*
import com.project.mobilemcm.pricing.data.ActionPricesDao
import com.project.mobilemcm.pricing.data.CompanyDao
import com.project.mobilemcm.pricing.data.DiscontsDao
import com.project.mobilemcm.pricing.data.IndividualPricesDao
import com.project.mobilemcm.pricing.data.ItemActionDao
import com.project.mobilemcm.pricing.data.ItemDao
import com.project.mobilemcm.pricing.data.ItemIndDao
import com.project.mobilemcm.pricing.model.ActionPrices
import com.project.mobilemcm.pricing.model.Company
import com.project.mobilemcm.pricing.model.Disconts
import com.project.mobilemcm.pricing.model.IndividualPrices
import com.project.mobilemcm.pricing.model.Item
import com.project.mobilemcm.pricing.model.ItemAction
import com.project.mobilemcm.pricing.model.ItemInd

@Database(
    entities = [Store::class, Category::class, Pricegroup::class, Pricegroups2::class,
        Good::class, Stock::class, RequestDocument::class, Counterparties::class,
        RequestGoods::class, Vendors::class, CounterpartiesStores::class, LoggedInUser::class,
        Disconts::class, Item::class, Company::class, ActionPrices::class, ItemAction::class,
        IndividualPrices::class, ItemInd::class, Division::class, ObmenDate::class, Contract::class],
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2,
            spec = AppDatabase.MyAutoMigration::class
        )
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    class MyAutoMigration : AutoMigrationSpec

    abstract fun storeDao(): StoreDao
    abstract fun categoryDao(): CategoryDao
    abstract fun pricegroupDao(): PricegroupDao
    abstract fun pricegroups2Dao(): Pricegroups2Dao
    abstract fun goodDao(): GoodDao
    abstract fun stockDao(): StockDao
    abstract fun requestDocumentDao(): RequestDocumentDao
    abstract fun counterpartiesDao(): CounterpartiesDao
    abstract fun requestGoodsDao(): RequestGoodsDao
    abstract fun vendorsDao(): VendorsDao
    abstract fun counterpartiesStoresDao(): CounterpartiesStoresDao
    abstract fun loggedInUserDao(): LoggedInUserDao
    abstract fun companyDao(): CompanyDao
    abstract fun discontsDao(): DiscontsDao
    abstract fun itemDao(): ItemDao
    abstract fun actionPricesDao(): ActionPricesDao
    abstract fun itemActionDao(): ItemActionDao
    abstract fun individualPricesDao(): IndividualPricesDao
    abstract fun itemIndDao(): ItemIndDao
    abstract fun divisionDao(): DivisionDao
    abstract fun obmenDateDao(): ObmenDateDao
    abstract fun contractsDao(): ContractsDao
}
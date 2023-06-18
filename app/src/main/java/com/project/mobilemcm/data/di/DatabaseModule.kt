package com.project.mobilemcm.data.di

import android.content.Context
import androidx.room.Room
import com.project.mobilemcm.data.local.database.*
import com.project.mobilemcm.pricing.data.ActionPricesDao
import com.project.mobilemcm.pricing.data.CompanyDao
import com.project.mobilemcm.pricing.data.DiscontsDao
import com.project.mobilemcm.pricing.data.IndividualPricesDao
import com.project.mobilemcm.pricing.data.ItemActionDao
import com.project.mobilemcm.pricing.data.ItemDao
import com.project.mobilemcm.pricing.data.ItemIndDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideLocalDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "localdb.db"
        ).build()
    }

    @Provides
    fun providesDivisionDao(appDatabase: AppDatabase): DivisionDao {
        return appDatabase.DivisionDao()
    }

    @Provides
    fun providesIndividualPricesDao(appDatabase: AppDatabase): IndividualPricesDao {
        return appDatabase.IndividualPricesDao()
    }

    @Provides
    fun providesItemIndDao(appDatabase: AppDatabase): ItemIndDao {
        return appDatabase.ItemIndDao()
    }

    @Provides
    fun providesActionPricesDao(appDatabase: AppDatabase): ActionPricesDao {
        return appDatabase.ActionPricesDao()
    }

    @Provides
    fun providesItemActionDao(appDatabase: AppDatabase): ItemActionDao {
        return appDatabase.ItemActionDao()
    }

    @Provides
    fun providesCompanyDao(appDatabase: AppDatabase): CompanyDao {
        return appDatabase.CompanyDao()
    }

    @Provides
    fun providesDiscontsDao(appDatabase: AppDatabase): DiscontsDao {
        return appDatabase.DiscontsDao()
    }

    @Provides
    fun providesItemDao(appDatabase: AppDatabase): ItemDao {
        return appDatabase.ItemDao()
    }

    @Provides
    fun providesLoggedInUserDao(appDatabase: AppDatabase): LoggedInUserDao {
        return appDatabase.LoggedInUserDao()
    }

    @Provides
    fun providesCounterpartiesStoresDao(appDatabase: AppDatabase): CounterpartiesStoresDao {
        return appDatabase.CounterpartiesStoresDao()
    }


    @Provides
    fun providesVendorsDao(appDatabase: AppDatabase): VendorsDao {
        return appDatabase.vendorsDao()
    }


    @Provides
    fun providesRequestGoodsDao(appDatabase: AppDatabase): RequestGoodsDao {
        return appDatabase.requestGoodsDao()
    }


    @Provides
    fun providesCounterpartiesDao(appDatabase: AppDatabase): CounterpartiesDao {
        return appDatabase.counterpartiesDao()
    }

    @Provides
    fun providesRequestDocumentDao(appDatabase: AppDatabase): RequestDocumentDao {
        return appDatabase.requestDocumentDao()
    }

    @Provides
    fun provideStoreDao(appDatabase: AppDatabase): StoreDao {
        return appDatabase.storeDao()
    }

    @Provides
    fun provideCategoryDao(appDatabase: AppDatabase): CategoryDao {
        return appDatabase.categoryDao()
    }

    @Provides
    fun providePricegroupDao(appDatabase: AppDatabase): PricegroupDao {
        return appDatabase.pricegroupDao()
    }

    @Provides
    fun providePricegroups2Dao(appDatabase: AppDatabase): Pricegroups2Dao {
        return appDatabase.pricegroups2Dao()
    }

    @Provides
    fun provideGoodDao(appDatabase: AppDatabase): GoodDao {
        return appDatabase.goodDao()
    }

    @Provides
    fun provideStockDao(appDatabase: AppDatabase): StockDao {
        return appDatabase.stockDao()
    }

}
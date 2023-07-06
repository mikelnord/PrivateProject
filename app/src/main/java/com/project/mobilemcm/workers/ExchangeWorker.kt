package com.project.mobilemcm.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.project.mobilemcm.data.Repository
import com.project.mobilemcm.data.login.LoginRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope


@HiltWorker
class ExchangeWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    val repository: Repository,
    val loginRepository: LoginRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            getObmen()
            if (!isError) {
                makeStatusNotification("Обмен с сервером выполнен успешно", applicationContext)
            } else {
                makeStatusNotification(strMessage, applicationContext)
            }
            Result.success()
        } catch (exception: Exception) {
            makeStatusNotification(exception.message.toString(), applicationContext)
            Result.retry()
        }
    }

    private var strMessage: String = ""
    private var isError = false

    private val errorHandler =
        CoroutineExceptionHandler { _, exception ->
            exception.printStackTrace()
            isError = true
            strMessage = exception.message.toString()
        }

    private suspend fun getObmen() {
        coroutineScope {
            val strPodr = loginRepository.user?.division_id//stock
            val strUserId = loginRepository.user?.id ?: ""
            val dateObmen = async(Dispatchers.IO) { repository.getObmenDate() }
            val fileObmen = async(Dispatchers.IO) {
                val result =
                    strPodr?.let {
                        repository.fetchObmenFile(
                            dateObmen.await()?.dateObmen ?: "2001-01-01T00:00:00",
                            it,
                            strUserId
                        )
                    }
                result?.let { res ->
                    if (res.status == com.project.mobilemcm.data.local.database.model.Result.Status.SUCCESS) {
                        isError = false
                        return@async result.data
                    } else {
                        isError = true
                        strMessage = result.message.toString()
                        return@async null
                    }
                }
            }

            if (!isError) {
                fileObmen.await()?.let {
                    try {
                        repository.addGoodToBase(it)
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
                        if (repository.getCountVendors())
                            repository.addVendors(repository.getAllVendors())
                    } catch (e: Throwable) {
                        isError = true
                        strMessage = e.message.toString()
                    }
                    if (!isError) {
                        repository.addDateObmenToBase(it)
                    }
                }
            }
        }
    }
}
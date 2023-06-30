package com.project.mobilemcm.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.project.mobilemcm.data.Repository
import com.project.mobilemcm.data.login.LoginRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException


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
            makeStatusNotification("Обмен с сервером выполнен успешно", applicationContext)
            Result.success()
        } catch (exception: Exception) {
            exception.printStackTrace()
            Result.failure()
        }
    }

    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    var isError = false
    private fun getObmen() {
        val strPodr = loginRepository.user?.division_id//stock
        val strUserId = loginRepository.user?.id ?: ""
        val dateObmen = scope.async { repository.getObmenDate() }
        val fileObmen = scope.async(Dispatchers.IO) {
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
                    //message.postValue(result.message)
                    return@async null
                }
            }
        }

        scope.launch() {
            fileObmen.await()?.let {
                try {
                    if (repository.addGoodToBase(it) != (fileObmen.await()?.goods?.size
                            ?: 0)
                    ) throw IOException("errorObmen")
                    repository.addCategoryToBase(it)
                    repository.addPricegroupToBase(it)
                    repository.addPricegroups2ToBase(it)
                    repository.addStoresToBase(it)
                    if (repository.addStockToBase(it) != (fileObmen.await()?.stocks?.size
                            ?: 0)
                    ) throw IOException("errorObmen")
                    repository.addCounterpartiesToBase(it)
                    repository.addCounterpartiesStoresToBase(it)
                    repository.addDiscontsToBase(it)
                    repository.addActionPricesToBase(it)
                    repository.addIndividualPricesToBase(it)
                    repository.addDivisionToBase(it)
                    repository.addDateObmenToBase(it)
                    if (repository.getCountVendors())
                        repository.addVendors(repository.getAllVendors())
                } catch (e: Throwable) {
                    Log.e("errorObmen", e.message.toString())
                }
            }
        }
    }

}

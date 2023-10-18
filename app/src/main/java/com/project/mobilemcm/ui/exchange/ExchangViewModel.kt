package com.project.mobilemcm.ui.exchange


import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.project.mobilemcm.data.Repository
import com.project.mobilemcm.data.login.LoginRepository
import com.project.mobilemcm.workers.ExchangeWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val version = "2"

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val repository: Repository,
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message = _message

    lateinit var idWork: UUID

    private var isError = false
    private val errorHandler =
        CoroutineExceptionHandler { _, exception ->
            exception.printStackTrace()
            isError = true
            message.postValue(exception.message.toString())
        }

    fun applyExchangeWorker(context: Context) {
        val workRequest =
            PeriodicWorkRequestBuilder<ExchangeWorker>(30, TimeUnit.MINUTES, 5, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                //.setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.MINUTES)
                .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "sendDateFromBase",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    fun applyWorker(context: Context) {
        val workRequest = OneTimeWorkRequestBuilder<ExchangeWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        idWork = workRequest.id

        WorkManager.getInstance(context).enqueueUniqueWork(
            "sendDateFromBaseOneTime",
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }

}
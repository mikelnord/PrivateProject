package com.project.mobilemcm.ui.home

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.project.mobilemcm.data.Repository
import com.project.mobilemcm.data.local.database.model.FileDownload
import com.project.mobilemcm.data.login.LoginRepository
import com.project.mobilemcm.workers.FileDownloadWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository,
    val loginRepository: LoginRepository
) : ViewModel() {

    private var _appMode: MutableLiveData<Boolean> =
        MutableLiveData(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
    val appMode = _appMode

    private var _emptyBase: MutableLiveData<Boolean> = MutableLiveData()
    val emptyBase = _emptyBase

    fun isLoginFirst() {
        viewModelScope.launch {
            _emptyBase.postValue(repository.firstLogin() == 0)
        }
    }

    fun setMode() {
        appMode.value?.let {
            if (it) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            _appMode.value = !it
        }
    }

    val dateObmen = repository.getFlowDate().asLiveData()


    fun startDownloadingFile(
        file: FileDownload,
        success: (String) -> Unit,
        failed: (String) -> Unit,
        running: () -> Unit,
        context: Context,
        owner: LifecycleOwner

    ) {
        val data = Data.Builder()

        data.apply {
            putString(FileDownloadWorker.FileParams.KEY_FILE_NAME, file.name)
            putString(FileDownloadWorker.FileParams.KEY_FILE_URL, file.url)
            putString(FileDownloadWorker.FileParams.KEY_FILE_TYPE, file.type)
        }

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val fileDownloadWorker = OneTimeWorkRequestBuilder<FileDownloadWorker>()
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()
        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniqueWork(
            "oneFileDownloadWork_${System.currentTimeMillis()}",
            ExistingWorkPolicy.KEEP,
            fileDownloadWorker
        )

        workManager.getWorkInfoByIdLiveData(fileDownloadWorker.id)
            .observe(owner) { info ->
                info?.let {
                    when (it.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            success(
                                it.outputData.getString(FileDownloadWorker.FileParams.KEY_FILE_URI)
                                    ?: ""
                            )
                        }

                        WorkInfo.State.FAILED -> {
                            failed("Downloading failed!")
                        }

                        WorkInfo.State.RUNNING -> {
                            running()
                        }

                        else -> {
                            failed("Something went wrong")
                        }
                    }
                }
            }
    }

    val data = MutableLiveData(
        FileDownload(
            id = "10",
            name = "UpdateMCM.apk",
            type = "APK",
            url = "https://data.mcmshop.ru/storage/mobile_app/app-release.apk",
            downloadedUri = null
        )
    )

}
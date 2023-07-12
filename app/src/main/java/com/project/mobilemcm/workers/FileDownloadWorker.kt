package com.project.mobilemcm.workers

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.project.mobilemcm.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import java.io.FileOutputStream
import java.net.URL

@HiltWorker
class FileDownloadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        val fileUrl = inputData.getString(FileParams.KEY_FILE_URL) ?: ""
        val fileName = inputData.getString(FileParams.KEY_FILE_NAME) ?: ""
        val fileType = inputData.getString(FileParams.KEY_FILE_TYPE) ?: ""

        Log.d("TAG", "doWork: $fileUrl | $fileName | $fileType")


        if (fileName.isEmpty()
            || fileType.isEmpty()
            || fileUrl.isEmpty()
        ) {
            Result.failure()
        }

        val name = NotificationConstants.CHANNEL_NAME
        val description = NotificationConstants.CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(NotificationConstants.CHANNEL_ID, name, importance)
        channel.description = description

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager?.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Downloading your file...")
            .setOngoing(true)
            .setProgress(0, 0, true)

        NotificationManagerCompat.from(context)
            .notify(NotificationConstants.NOTIFICATION_ID, builder.build())

        val uri = getSavedFileUri(
            fileName = fileName,
            fileUrl = fileUrl
        )

        NotificationManagerCompat.from(context).cancel(NotificationConstants.NOTIFICATION_ID)
        return Result.success(workDataOf(FileParams.KEY_FILE_URI to uri.toString()))

    }

    private fun getSavedFileUri(
        fileName: String,
        fileUrl: String
    ): Uri {

        val target = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        URL(fileUrl).openStream().use { input ->
            FileOutputStream(target).use { output ->
                input.copyTo(output)
            }
        }

        return target.toUri()
    }


    object FileParams {
        const val KEY_FILE_URL = "key_file_url"
        const val KEY_FILE_TYPE = "key_file_type"
        const val KEY_FILE_NAME = "key_file_name"
        const val KEY_FILE_URI = "key_file_uri"
    }

    object NotificationConstants {
        const val CHANNEL_NAME = "download_file_worker_demo_channel"
        const val CHANNEL_DESCRIPTION = "download_file_worker_demo_description"
        const val CHANNEL_ID = "download_file_worker_demo_channel_123456"
        const val NOTIFICATION_ID = 1
    }
}


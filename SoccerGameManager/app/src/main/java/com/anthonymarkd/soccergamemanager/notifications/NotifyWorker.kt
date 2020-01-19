package com.anthonymarkd.soccergamemanager.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters


class NotifyWorker(context: Context, params: WorkerParameters) :
    Worker(context, params) {
    override fun doWork(): Result { // Method to trigger an instant notification
       // triggerNotification()
        return Result.success()
        // (Returning RETRY tells WorkManager to try this task again
// later; FAILURE says not to try again.)
    }
}
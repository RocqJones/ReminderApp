package com.rocqjones.reminderapp.ui.vm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.rocqjones.reminderapp.data.DataSource
import com.rocqjones.reminderapp.work.ReminderWorker
import java.util.concurrent.TimeUnit

class ReminderViewModel(application: Application): ViewModel() {

    private val itemsList = DataSource.plants
    private val workManager = WorkManager.getInstance(application)

    internal fun scheduleReminder(
        duration: Long,
        unit: TimeUnit,
        plantName: String
    ) {
        // create a Data instance with the plantName passed to it
        val myWorkRequestBuilder = OneTimeWorkRequestBuilder<ReminderWorker>()
        for (items in itemsList.toMutableList()) {
            if (items.name == plantName) {
                myWorkRequestBuilder.setInputData(
                    workDataOf(
                        "NAME" to items.name,
                        "MESSAGE" to items.description
                    )
                )
            }
        }
        myWorkRequestBuilder.setInitialDelay(duration, unit)
        workManager.enqueue(myWorkRequestBuilder.build())
    }
}

class ReminderViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            ReminderViewModel(application) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
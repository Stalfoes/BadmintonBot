package com.anthonymarkd.soccergamemanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class DivisionRepository(private val divisionDao: DivisionDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allDivisions: LiveData<List<Division>> = divisionDao.getDivisions()

    suspend fun insert(divisions: List<Division>) {
        divisionDao.insertAllDivisions(divisions)
    }
}
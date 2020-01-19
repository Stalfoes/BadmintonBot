package com.anthonymarkd.soccergamemanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DivisionDao {
    @Query("SELECT * from Division")
    fun getDivisions(): LiveData<List<Division>>

    @Insert
    suspend fun insertAllDivisions(divisions: List<Division>)

}
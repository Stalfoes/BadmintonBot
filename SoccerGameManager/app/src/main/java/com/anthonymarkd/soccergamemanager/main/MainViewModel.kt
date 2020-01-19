package com.anthonymarkd.soccergamemanager.main

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.*
import com.anthonymarkd.soccergamemanager.Division
import com.anthonymarkd.soccergamemanager.DivisionRepository
import com.anthonymarkd.soccergamemanager.SoccerDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.lang.Exception

class MainViewModel(application: Application) : AndroidViewModel(application) {


    val divisions: LiveData<List<Division>>
    val loading: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    var errorState = MutableLiveData<Boolean>()
    private val repository: DivisionRepository

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val divisionDao = SoccerDatabase.getDatabase(
            application
        ).divsionDao()

        repository = DivisionRepository(divisionDao)
        divisions = repository.allDivisions
    }

    fun launchDataLoad() {
        viewModelScope.async {
            println(divisions.value)
            if (divisions.value.isNullOrEmpty()) {
                println("load new data")
                if (isOnline(getApplication())) {
                    try {
                        loading.value = true
                        loadData()
                    } catch (e: Exception) {
                        errorState.value = true
                    }
                } else {
                    errorState.value = true
                }
            }else{
                println("no new data")
            }
        }

    }


    suspend fun loadData() {
        val document = withContext(Dispatchers.IO) {
            Jsoup.connect("https://www.edsa.org/Schedules-Standings").get()
        }
        val divisions = mutableListOf<Division>()
        println(String.format("test1"))
        val links =
            document.select("#treemenu1 > li:nth-child(1) > ul > li:nth-child(1) > ul > li > a")
        println(links.size)
        for (link in links) {
            val division =
                Division(link.text(), link.attr("href"))
            divisions.add(division)
        }
        repository.insert(divisions)
        loading.value = false
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

}
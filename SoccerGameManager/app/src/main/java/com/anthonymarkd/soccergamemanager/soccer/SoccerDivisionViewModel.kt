package com.anthonymarkd.soccergamemanager.soccer

import kotlinx.coroutines.*
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.*
import com.anthonymarkd.soccergamemanager.*
import java.lang.Exception


class SoccerDivisionViewModel(val url: String, application: Application) : ViewModel() {

    private var gameDaySchedule: LiveData<List<GameDay>>
    private var gameDayFutureSchedule: LiveData<List<GameDay>>
    val teamStandings: LiveData<List<TeamStanding>>
    val teams: LiveData<List<String>>
    val games = MediatorLiveData<List<GameDay>>()
    private val repository: GameDayRepository
    val loading: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val refreshState: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    var errorState = MutableLiveData<Boolean>()

    private var selection = "games"
    private var firstTime = true

    init {
        val gameDayDao = SoccerDatabase.getDatabase(application).gameDayDao()
        repository = GameDayRepository(gameDayDao)
        gameDaySchedule = repository.getAllGameDays(url)
        gameDayFutureSchedule = repository.getAllFutureGameDays(url)
        teamStandings = repository.getAllTeamStandings(url)
        teams = repository.getAllTeams(url)

        games.addSource(gameDaySchedule) { result ->
            if (selection == "games") {
                if (result.isNullOrEmpty()) {
                    if (firstTime) {
                        viewModelScope.launch {
                            if (isOnline(application)) {
                                try {
                                    loading.value = true
                                    repository.scrapeDataAsync(url).await()
                                } catch (e: Exception) {
                                    errorState.value = true
                                    e.printStackTrace()
                                    loading.value = false
                                }
                            }else{
                                errorState.value = true
                            }
                        }
                    }
                } else {
                    result?.let { games.value = it }
                }
                firstTime = false
            }
        }
        games.addSource(gameDayFutureSchedule) { result ->
            if (selection == "futureGames") {
                if (result.isNullOrEmpty()) {

                } else {
                    result?.let { games.value = it }
                }
            }
        }

    }

    private fun swapListView(schedule: String) = when (schedule) {
        "games" -> gameDaySchedule.value?.let { games.value = it }
        else -> gameDayFutureSchedule.value?.let { games.value = it }


    }.also { selection = schedule }

    fun getSchedule() {
        swapListView("games")
    }

    fun getNextGames() {
        swapListView("futureGames")
    }

    fun refreshData() {
        println("refreshing data")
        viewModelScope.async {
            try {
                repository.scrapeDataAsync(url).await()
            } catch (e: Exception) {
                errorState.value = true
                e.printStackTrace()
            }
            refreshState.value = false
        }
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }


}



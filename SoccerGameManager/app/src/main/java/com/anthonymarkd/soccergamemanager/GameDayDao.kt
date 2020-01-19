package com.anthonymarkd.soccergamemanager

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import java.nio.file.Files.delete


@Dao
interface GameDayDao {
    @Query("SELECT * FROM GameDay WHERE divisionUrl =:divisionUrl ORDER BY gameDate ASC")
    fun getGameDays(divisionUrl: String): LiveData<List<GameDay>>

    @Query("SELECT * FROM GameDay WHERE divisionUrl =:divisionUrl AND datetime(gameDate/1000, 'unixepoch') >= datetime('now') ORDER BY gameDate ASC")
    fun getFutureGameDays(divisionUrl: String): LiveData<List<GameDay>>

    @Query("SELECT * FROM TeamStanding WHERE divisionUrl =:divisionUrl ORDER BY points DESC")
    fun getTeamStandings(divisionUrl: String): LiveData<List<TeamStanding>>

    @Query("SELECT teamName FROM TeamStanding WHERE divisionUrl =:divisionUrl")
    fun getTeams(divisionUrl: String): LiveData<List<String>>

    @Query("DELETE FROM GameDay WHERE divisionUrl =:divisionUrl")
    suspend fun deleteByDivisionGameDay(divisionUrl: String)

    @Query("DELETE FROM TeamStanding WHERE divisionUrl =:divisionUrl")
    suspend fun deleteByDivisionTeamStanding(divisionUrl: String)

    @Insert
    suspend fun insertAllGameDays(gameDays: List<GameDay>)

    @Insert
    suspend fun insertAllTeamStandings(teamStandings: List<TeamStanding>)

    @Transaction
    suspend fun insertAndDeleteGameDaysInTransaction(divisionUrl: String, gameDays: List<GameDay>,teamStandings: List<TeamStanding>) {
        // Anything inside this method runs in a single transaction.
        deleteByDivisionGameDay(divisionUrl)
        deleteByDivisionTeamStanding(divisionUrl)
        insertAllGameDays(gameDays)
        insertAllTeamStandings(teamStandings)
    }
}
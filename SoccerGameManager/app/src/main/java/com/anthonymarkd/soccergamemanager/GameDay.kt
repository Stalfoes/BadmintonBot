package com.anthonymarkd.soccergamemanager

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

@Entity
@TypeConverters(Converters::class)
class GameDay(
    var gameDate: Date?,
    var gameDateString: String?,
    var homeTeam: String?,
    var awayTeam: String?,
    var homeTeamScore: String?,
    var awayTeamScore: String?,
    var homeShirtColor: String?,
    var homeShortsColor: String?,
    var awayShirtColor: String?,
    var awayShortsColor: String?,
    var gameField: String?,
    var gameLocation: String?,
    var divisionUrl: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    fun printGameDay() {
        val gameDayOutput = String.format("Home: %s, Away: %s, %s", homeTeam, awayTeam, gameDate)
        println(gameDayOutput)
    }

    fun getMatchResults(): Pair<TeamStanding, TeamStanding> {
        lateinit var homeTeamStanding: TeamStanding
        lateinit var awayTeamStanding: TeamStanding
        val homeTeamScore = homeTeamScore!!.toInt()
        val awayTeamScore = awayTeamScore!!.toInt()
        when {
            // Home Team Won
            homeTeamScore > awayTeamScore -> {
                homeTeamStanding =
                    TeamStanding(
                        homeTeam,
                        3,
                        1,
                        0,
                        0,
                        homeTeamScore,
                        awayTeamScore,
                        "Standing",
                        divisionUrl!!
                    )
                awayTeamStanding =
                    TeamStanding(
                        awayTeam,
                        0,
                        0,
                        1,
                        0,
                        awayTeamScore,
                        homeTeamScore,
                        "Standing",
                        divisionUrl!!
                    )
            }
            // Away Team Won
            homeTeamScore < awayTeamScore -> {
                homeTeamStanding =
                    TeamStanding(
                        homeTeam,
                        0,
                        0,
                        1,
                        0,
                        homeTeamScore,
                        awayTeamScore,
                        "Standing",
                        divisionUrl!!
                    )
                awayTeamStanding =
                    TeamStanding(
                        awayTeam,
                        3,
                        1,
                        0,
                        0,
                        awayTeamScore,
                        homeTeamScore,
                        "Standing",
                        divisionUrl!!
                    )
            }
            else -> {
                // They Tied
                homeTeamStanding =
                    TeamStanding(
                        homeTeam,
                        1,
                        0,
                        0,
                        1,
                        homeTeamScore,
                        awayTeamScore,
                        "Standing",
                        divisionUrl!!
                    )
                awayTeamStanding =
                    TeamStanding(
                        awayTeam,
                        1,
                        0,
                        0,
                        1,
                        awayTeamScore,
                        homeTeamScore,
                        "Standing",
                        divisionUrl!!
                    )
            }
        }
        return Pair(homeTeamStanding, awayTeamStanding)

    }

}

package com.anthonymarkd.soccergamemanager

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import org.jsoup.Jsoup
import java.text.ParseException
import java.text.SimpleDateFormat

import kotlin.collections.HashMap
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.*


class GameDayRepository(private val gameDayDao: GameDayDao) {


    fun getAllGameDays(divisionUrl: String): LiveData<List<GameDay>> {
        return gameDayDao.getGameDays(divisionUrl)
    }

    fun getAllFutureGameDays(divisionUrl: String): LiveData<List<GameDay>> {
        return gameDayDao.getFutureGameDays(divisionUrl)
    }

    fun getAllTeamStandings(divisionUrl: String): LiveData<List<TeamStanding>> {
        return gameDayDao.getTeamStandings(divisionUrl)
    }

    fun getAllTeams(divisionUrl: String): LiveData<List<String>> {
        return gameDayDao.getTeams(divisionUrl)
    }

    private suspend fun refreshData(
        divisionUrl: String,
        gameDays: List<GameDay>,
        teamStandings: List<TeamStanding>
    ) {
        gameDayDao.insertAndDeleteGameDaysInTransaction(divisionUrl, gameDays, teamStandings)
    }

    suspend fun scrapeDataAsync(divisionUrl: String) = withContext(Dispatchers.IO) {
        async {
            val gameDays = mutableListOf<GameDay>()
            try {
                val document = Jsoup.connect(divisionUrl).get()


                println(String.format("Title: %s\n", document.title()))

                val games = document.getElementsByClass("wGridRow")
                val gamesAlt = document.getElementsByClass("wGridRowAlt")
                val gamesCurr = document.getElementsByClass("wGridCurrentRow")
                val gamesCurrAlt = document.getElementsByClass("wGridCurrentRowAlt")
                val gamesGameDay = document.getElementsByClass("wGridGameRow")
                val gamesGameDayAlt = document.getElementsByClass("wGridGameRowAlt")
                // System.out.println(games.size());
                // System.out.println(gamesAlt.size());
                games.addAll(gamesAlt)
                games.addAll(gamesCurr)
                games.addAll(gamesCurrAlt)
                games.addAll(gamesGameDay)
                games.addAll(gamesGameDayAlt)
                println(games.size)
                for (soccerGame in games) {

                    val homeTeam =
                        soccerGame.getElementsByClass("wHomeName").text().replace(" Home", "")
                    val awayTeam = soccerGame.getElementsByClass("wVisitorName").text()
                    val gameDate = soccerGame.getElementsByClass("wGameDateTime").text()
                    val homeScore = soccerGame.getElementsByClass("wScoreHome").text()
                    val awayScore = soccerGame.getElementsByClass("wScoreVisitors").text()
                    val homeColors = soccerGame.getElementsByClass("wHomeColors").first()
                    val gameField = soccerGame.getElementsByClass("wFieldName").text()
                    val gameLocation =
                        soccerGame.getElementsByClass("wFieldName").select("a").attr("href")


                    val homeShirtColor =
                        homeColors.getElementsByClass("wcolorshirta").attr("style").toString()
                            .substringAfter("background: ").replace(";", "")
                    val homeShortsColor =
                        homeColors.getElementsByClass("wcolorshorta").attr("style").toString()
                            .substringAfter("background: ").replace(";", "")
                    val awayColors = soccerGame.getElementsByClass("wVisitorColors").first()
                    val awayShirtColor =
                        awayColors.getElementsByClass("wcolorshirta").attr("style").toString()
                            .substringAfter("background: ").replace(";", "")
                    val awayShortsColor =
                        awayColors.getElementsByClass("wcolorshorta").attr("style").toString()
                            .substringAfter("background: ").replace(";", "")


                    //println(homeShirtColor)
                    //val homeShirtColor = homeColors.get
                    val gameDateTime = dateConverter(gameDate)
                    val gameDateString = dateToString(gameDateTime)
                    val gameDay = GameDay(
                        gameDateTime,
                        gameDateString,
                        homeTeam,
                        awayTeam,
                        homeScore,
                        awayScore,
                        homeShirtColor,
                        homeShortsColor,
                        awayShirtColor,
                        awayShortsColor,
                        gameField,
                        gameLocation,
                        divisionUrl
                    )
                    gameDays.add(gameDay)
                }
                val teamStandings = calculateStandings(divisionUrl, gameDays)
                refreshData(divisionUrl, gameDays, teamStandings)

            } catch (e: Exception) {
                throw e
            }
        }
    }

    private fun calculateStandings(
        divisionUrl: String,
        gameDays: MutableList<GameDay>
    ): List<TeamStanding> {

        val teamStandingsList = mutableListOf<TeamStanding>()

        val teamStandingsHashMap = HashMap<String?, TeamStanding>()

        for (game in (gameDays)) {
            if (game.homeTeamScore!!.isDigitsOnly() && game.awayTeamScore!!.isDigitsOnly()) {
                val (homeTeamStanding, awayTeamStanding) = game.getMatchResults()
                if (teamStandingsHashMap.containsKey(homeTeamStanding.teamName)) {

                    val prevHomeTeamStanding = teamStandingsHashMap[game.homeTeam]
                    val newHomeTeamStanding = TeamStanding(
                        prevHomeTeamStanding!!.teamName,
                        prevHomeTeamStanding.points + homeTeamStanding.points,
                        prevHomeTeamStanding.gamesWon + homeTeamStanding.gamesWon,
                        prevHomeTeamStanding.gamesLost + homeTeamStanding.gamesLost,
                        prevHomeTeamStanding.gamesTied + homeTeamStanding.gamesTied,
                        prevHomeTeamStanding.goalsFor + homeTeamStanding.goalsFor,
                        prevHomeTeamStanding.goalAgainst + homeTeamStanding.goalAgainst
                        , "Standing", divisionUrl
                    )
                    teamStandingsHashMap[game.homeTeam] = newHomeTeamStanding
                } else {

                    teamStandingsHashMap[homeTeamStanding.teamName] = homeTeamStanding
                }
                if (teamStandingsHashMap.containsKey(awayTeamStanding.teamName)) {

                    val prevAwayTeamStanding = teamStandingsHashMap[game.awayTeam]
                    val newAwayTeamStanding = TeamStanding(
                        prevAwayTeamStanding!!.teamName,
                        prevAwayTeamStanding.points + awayTeamStanding.points,
                        prevAwayTeamStanding.gamesWon + awayTeamStanding.gamesWon,
                        prevAwayTeamStanding.gamesLost + awayTeamStanding.gamesLost,
                        prevAwayTeamStanding.gamesTied + awayTeamStanding.gamesTied,
                        prevAwayTeamStanding.goalsFor + awayTeamStanding.goalsFor,
                        prevAwayTeamStanding.goalAgainst + awayTeamStanding.goalAgainst,
                        "Standing", divisionUrl
                    )
                    teamStandingsHashMap[game.awayTeam] = newAwayTeamStanding
                } else {

                    teamStandingsHashMap[awayTeamStanding.teamName] = awayTeamStanding
                }
            }

        }
        for (value in teamStandingsHashMap.values) {
            teamStandingsList.add(value)
        }
        return teamStandingsList
    }

    private fun dateConverter(date: String): Date? {
        // Use locality, or it crashes on google pixel 2, Paul's phone
        val simpleDateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy hh:mm a", Locale.US)
        var parse: Date? = null
        try {
            parse = simpleDateFormat.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return parse
    }

    fun dateToString(date: Date?): String {
        return if (date != null) {
            val formatter = SimpleDateFormat("MMMM dd, yyyy    hh:mm a")
            val output = formatter.format(date)
            output
        } else {
            ""
        }

    }

}
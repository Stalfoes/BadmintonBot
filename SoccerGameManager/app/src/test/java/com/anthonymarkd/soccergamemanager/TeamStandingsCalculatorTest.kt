package com.anthonymarkd.soccergamemanager

import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TeamStandingsCalculatorTest {
    @Test
    fun testGetMatchResults() {
        val game = GameDay(
            Calendar.getInstance().time,
            "home",
            "away",
            "1",
            "1",
            "#FFFFFF",
            "#FFFFFF",
            "#FFFFFF",
            "#FFFFFF", "", "", ""
        )
        val game2 = GameDay(
            Calendar.getInstance().time,
            "home",
            "away",
            "0",
            "1",
            "#FFFFFF",
            "#FFFFFF",
            "#FFFFFF",
            "#FFFFFF",
            "", "", ""
        )
        val game3 = GameDay(
            Calendar.getInstance().time,
            "home",
            "away",
            "1",
            "0",
            "#FFFFFF",
            "#FFFFFF",
            "#FFFFFF",
            "#FFFFFF", "", "", ""
        )
        game.getMatchResults()
        val (homeGame1, awayGame1) = game.getMatchResults()
        val (homeGame2, awayGame2) = game2.getMatchResults()
        val (homeGame3, awayGame3) = game3.getMatchResults()
        val (testHomeTeamStanding, testAwayTeamStanding) = Pair(
            TeamStanding("home", 1, 0, 0, 1, 1, 1, "Standing", "")
            , TeamStanding("away", 1, 0, 0, 1, 1, 1, "Standing", "")
        )
        val (testHomeTeamStanding2, testAwayTeamStanding2) = Pair(
            TeamStanding("home", 0, 0, 1, 0, 0, 1, "Standing", "")
            , TeamStanding("away", 3, 1, 0, 0, 1, 0, "Standing", "")
        )
        val (testHomeTeamStanding3, testAwayTeamStanding3) = Pair(
            TeamStanding("home", 3, 1, 0, 0, 1, 0, "Standing", "")
            , TeamStanding("away", 0, 0, 1, 0, 0, 1, "Standing", "")
        )
        assertEquals(homeGame1, testHomeTeamStanding)
        assertEquals(homeGame2, testHomeTeamStanding2)
        assertEquals(homeGame3, testHomeTeamStanding3)
        assertEquals(awayGame1, testAwayTeamStanding)
        assertEquals(awayGame2, testAwayTeamStanding2)
        assertEquals(awayGame3, testAwayTeamStanding3)
    }
}
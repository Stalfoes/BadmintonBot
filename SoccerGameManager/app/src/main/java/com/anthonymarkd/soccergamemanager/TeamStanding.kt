package com.anthonymarkd.soccergamemanager

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class TeamStanding(
    var teamName: String?,
    var points: Int,
    var gamesWon: Int,
    var gamesLost: Int,
    var gamesTied: Int,
    var goalsFor: Int,
    var goalAgainst: Int,
    var type: String,
    var divisionUrl: String
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is TeamStanding) {
            return false
        }
        return (this.teamName == other.teamName &&
                this.points == other.points &&
                this.gamesWon == other.gamesWon &&
                this.gamesLost == other.gamesLost &&
                this.gamesTied == other.gamesTied &&
                this.goalsFor == other.goalsFor &&
                this.goalAgainst == other.goalAgainst &&
                this.type == other.type)


    }
}
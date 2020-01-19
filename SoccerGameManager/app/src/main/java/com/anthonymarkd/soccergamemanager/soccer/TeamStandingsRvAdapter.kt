package com.anthonymarkd.soccergamemanager.soccer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anthonymarkd.soccergamemanager.R
import com.anthonymarkd.soccergamemanager.TeamStanding
import kotlinx.android.synthetic.main.item_team_standing.view.*


class TeamStandingsRvAdapter(val teamStandings: List<TeamStanding>, val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return teamStandings.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (teamStandings[position].type == "Header") {
            0
        } else {
            1
        }
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous

    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            0 -> {
                TeamStandingViewHolderHeader(
                    LayoutInflater.from(context).inflate(
                        R.layout.item_team_standings_header,
                        parent,
                        false
                    )
                )
            }
            else -> {
                TeamStandingViewHolder(
                    LayoutInflater.from(context).inflate(
                        R.layout.item_team_standing,
                        parent,
                        false
                    )
                )
            }
        }

    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 -> {
                val holderTeamStandingHeader = holder as TeamStandingViewHolderHeader

            }
            else -> {
                val holderTeamStanding = holder as TeamStandingViewHolder
                holderTeamStanding.tvTeamName.text = teamStandings[position].teamName
                holderTeamStanding.tvGamesPlayed.text =
                    teamStandings[position].points.toString()
                holderTeamStanding.tvGamesWon.text = teamStandings[position].gamesWon.toString()
                holderTeamStanding.tvGamesLost.text = teamStandings[position].gamesLost.toString()
                holderTeamStanding.tvGamesTied.text = teamStandings[position].gamesTied.toString()
                holderTeamStanding.tvGoalsFor.text = teamStandings[position].goalsFor.toString()
                holderTeamStanding.tvGoalsAgainst.text = teamStandings[position].goalAgainst.toString()
            }
        }


    }


}

class TeamStandingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val tvTeamName: TextView = view.teamName
    val tvGamesPlayed: TextView = view.gamesPlayed
    val tvGamesWon: TextView = view.gamesWon
    val tvGamesLost: TextView = view.gamesLost
    val tvGamesTied: TextView = view.gamesTied
    val tvGoalsFor: TextView = view.goalsFor
    val tvGoalsAgainst: TextView = view.goalsAgainst

}

class TeamStandingViewHolderHeader(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to

}

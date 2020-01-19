package com.anthonymarkd.soccergamemanager.soccer

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anthonymarkd.soccergamemanager.GameDay
import com.anthonymarkd.soccergamemanager.R
import kotlinx.android.synthetic.main.item_soccergame.view.*
import java.lang.Exception

import java.text.SimpleDateFormat
import java.util.*


class SoccerGameRvAdapter(val games: MutableList<GameDay>, val context: Context) :
    RecyclerView.Adapter<SoccerGameRvAdapter.ViewHolder>() {
    var onItemClick: ((GameDay) -> Unit)? = null
    private var gamesCopy: MutableList<GameDay> = mutableListOf()
    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return games.size
    }

    init {
        gamesCopy.addAll(games)
    }


    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_soccergame,
                parent,
                false
            )
        )
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvHomeTeamName.text = games[position].homeTeam
        holder.tvAwayTeamName.text = games[position].awayTeam
        holder.tvHomeTeamScore.text = games[position].homeTeamScore
        holder.tvAwayTeamScore.text = games[position].awayTeamScore
        holder.tvGameDate.text = games[position].gameDateString
        holder.tvGameLocation.text = games[position].gameField


    }

    fun getColorFromString(colorString: String?): Int {
        return try {
            Color.parseColor(colorString)
        } catch (e: Exception) {
            //e.printStackTrace()
            android.R.color.white
        }

    }

    fun filter(text: String) {

        games.clear()
        if (text.isEmpty()) {
            games.addAll(gamesCopy)
        } else {
            for (game in gamesCopy) {
                if (game.homeTeam!!.toLowerCase().contains(text.toLowerCase()) || game.awayTeam!!.toLowerCase().contains(
                        text
                    )
                ) {
                    games.add(game)
                }
            }
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val tvHomeTeamName: TextView = view.homeTeamName
        val tvAwayTeamName: TextView = view.awayTeamName
        val tvHomeTeamScore: TextView = view.homeTeamScore
        val tvAwayTeamScore: TextView = view.awayTeamScore
        val tvGameDate: TextView = view.soccerGameDate
        val tvGameLocation: TextView = view.gameLocation

        init {
            tvGameLocation.setOnClickListener {
                onItemClick?.invoke(games[adapterPosition])
            }
        }
    }
}


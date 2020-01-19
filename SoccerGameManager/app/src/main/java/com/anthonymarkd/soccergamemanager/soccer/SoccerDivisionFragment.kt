package com.anthonymarkd.soccergamemanager.soccer

import android.R.attr.data
import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils.loadLayoutAnimation
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anthonymarkd.soccergamemanager.*
import com.anthonymarkd.soccergamemanager.settings.SettingsParentFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.alert_dialog_reminders.*
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.android.synthetic.main.main_fragment.view.*


class SoccerDivisionFragment : Fragment() {
    private lateinit var viewModel: SoccerDivisionViewModel
    private lateinit var url: String
    private lateinit var divisionName: String
    private var showStandings: Boolean = false
    private val newGamesList = mutableListOf<GameDay>()
    private var standingsList = mutableListOf<TeamStanding>()
    private var teamsList = mutableListOf<String>()
    var choices =
        arrayOf<String>("Choice1", "Choice2", "Choice3")
    var choicesInitial = booleanArrayOf(false, true, false)

    companion object {
        fun newInstance(url: String?, divisionName: String?) = SoccerDivisionFragment().apply {
            arguments = Bundle().apply {
                putString("Key_URL", url)
                putString("Key_DivisionName", divisionName)

            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("showStandings", showStandings)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.getString("Key_URL")?.let {
            url = it
            println(url)
        }
        arguments?.getString("Key_DivisionName")?.let {
            divisionName = it
        }
        showStandings = savedInstanceState?.getBoolean("showStandings", false) ?: false
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(url, activity!!.application)
        ).get(SoccerDivisionViewModel::class.java)
        val view = inflater.inflate(R.layout.main_fragment, container, false)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(view.toolbar)

        if ((activity as AppCompatActivity).supportActionBar != null) {
            (activity as AppCompatActivity).supportActionBar?.title = divisionName

            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
            view.toolbar.setTitleTextAppearance(context, R.style.AppThemeDark)
            view.toolbar.navigationIcon?.setColorFilter(
                ContextCompat.getColor(
                    context!!,
                    R.color.material_on_surface_emphasis_high_type
                ), PorterDuff.Mode.SRC_ATOP
            )

            println(" toolbar found")
        } else {
            println("no toolbar found")
        }
        setChipTextAppearance(view)
        setChipClickListeners(view)
        view.gamesRv.layoutManager = LinearLayoutManager(context)
        view.gamesRv.adapter =
            SoccerGameRvAdapter(
                mutableListOf(),
                context!!
            )
        addItemDecorationToRecyclerView(
            view.gamesRv,
            R.dimen.default_padding,
            R.dimen.default_padding,
            R.dimen.default_padding,
            R.dimen.default_padding_bottom
        )
        view.gamesRv.setItemViewCacheSize(20)
        view.swipe_refresh_layout!!.setOnRefreshListener {
            viewModel.refreshData()
        }

        val gamesObserver = Observer<List<GameDay>> { newGames ->
            // Update the UI
            // Access the RecyclerView Adapter and load the data into it
            val searchView =
                view.toolbar.menu.findItem(R.id.action_search).actionView as SearchView
            println("got new games")
            if (!newGames.isNullOrEmpty()) {
                if (!showStandings) {
                    gamesRv.setHasFixedSize(true)
                    newGamesList.clear()
                    newGamesList.addAll(newGames)
                    gamesRv.adapter =
                        SoccerGameRvAdapter(
                            newGamesList,
                            context!!
                        )
                    (gamesRv.adapter as SoccerGameRvAdapter).onItemClick = { game ->
                        println("do we hit?")
                        println(game.gameLocation)
                        val gmmIntentUri = Uri.parse(game.gameLocation)

                        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        // Make the Intent explicit by setting the Google Maps package
                        mapIntent.setPackage("com.google.android.apps.maps")
                        // Attempt to start an activity that can handle the Intent
                        if (mapIntent.resolveActivity(context!!.packageManager) != null) {
                            startActivity(mapIntent)
                        }


                    }
                    println("got new games2")
                    println(searchView.query.toString())
                    if (searchView.query.toString().isNotEmpty()) {
                        println("filtering")
                        (gamesRv.adapter as SoccerGameRvAdapter).filter(searchView.query.toString())
                    }
                    runLayoutAnimation(gamesRv)
                    viewModel.loading.value = false
                }
            } else {
                gamesRv.adapter =
                    SoccerGameRvAdapter(
                        newGamesList,
                        context!!
                    )
            }

        }
        val loadingStateObserver = Observer<Boolean> { loadingState ->
            if (loadingState == true) {
                println("RUNNING LOADING ANIM")
                shimmer_view_container.visibility = View.VISIBLE
                shimmer_view_container.startShimmer()
                gamesRv.visibility = View.INVISIBLE
            } else {
                shimmer_view_container.visibility = View.GONE
                shimmer_view_container.stopShimmer()
                gamesRv.visibility = View.VISIBLE
            }
        }
        val refreshingState = Observer<Boolean> { refreshState ->
            if (!refreshState) {
                view.swipe_refresh_layout.isRefreshing = false
            }
        }
        val teamListObserver = Observer<List<String>> { newTeamList ->
            teamsList = newTeamList.toMutableList()
        }
        val teamStandingsObserver = Observer<List<TeamStanding>> { newStandings ->
            standingsList = newStandings.toMutableList()
            standingsList.add(
                0,
                TeamStanding("Team Name", 0, 0, 0, 0, 0, 0, "Header", url)
            )
            if (gamesRv.adapter is TeamStandingsRvAdapter && !newStandings.isNullOrEmpty()) {
                viewModel.loading.value = false
                gamesRv.adapter =
                    TeamStandingsRvAdapter(
                        standingsList,
                        context!!
                    )
                runLayoutAnimation(gamesRv)
            }

        }

        viewModel.loading.observe(this, loadingStateObserver)
        viewModel.games.observe(this, gamesObserver)
        viewModel.teamStandings.observe(this, teamStandingsObserver)
        viewModel.refreshState.observe(this, refreshingState)
        viewModel.teams.observe(this, teamListObserver)
        viewModel.errorState.observe(this, Observer<Boolean> { errorState ->
            errorState?.let {
                if (errorState) {
                    viewModel.errorState.value = false
                    Snackbar.make(
                        view.rootView,
                        "Error loading soccer data. Please try again later.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        })
        if (savedInstanceState == null) {

        }


        return view

    }

    private fun runLayoutAnimation(recyclerView: RecyclerView) {
        val context = recyclerView.context
        val controller =
            loadLayoutAnimation(context, R.anim.layout_animation_fall_down)

        recyclerView.layoutAnimation = controller
        recyclerView.adapter!!.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.toolbar_menu, menu)


        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        // listening to search query text change
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // filter recycler view when query submitted
                if (gamesRv.adapter is SoccerGameRvAdapter) {
                    (gamesRv.adapter as SoccerGameRvAdapter).filter(query)
                }
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                // filter recycler view when text is changed
                println("do we reach the filter?")
                if (gamesRv.adapter is SoccerGameRvAdapter) {
                    (gamesRv.adapter as SoccerGameRvAdapter).filter(query)
                }
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // The action bar home/up action should open or close the drawer.

        when (item.itemId) {
            R.id.action_reminders -> {
                val dialogView = MaterialAlertDialogBuilder(context).setTitle("Set Reminder")
                    .setPositiveButton("Ok") { dialog, which ->

                    }
                    .setNegativeButton("Cancel", null)
                    .setView(R.layout.alert_dialog_reminders)
                    .create()
                dialogView.setOnShowListener {

                    ArrayAdapter(
                        this.context!!, android.R.layout.simple_spinner_item, teamsList
                    ).also { adapter ->
                        // Specify the layout to use when the list of choices appears
                        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_custom)
                        // Apply the adapter to the spinner
                        dialogView.spinner2.adapter = adapter
                    }

                }
                dialogView.show()


            }
            R.id.action_settings -> {
                activity!!.supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.container,
                        SettingsParentFragment()
                    ).addToBackStack(null)
                    .commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setChipTextAppearance(v: View) {
        v.ChipNextGames.setTextAppearance(R.style.chipTextAppearance)
        v.ChipSchedule.setTextAppearance(R.style.chipTextAppearance)
        v.ChipStandings.setTextAppearance(R.style.chipTextAppearance)
    }

    private fun setChipClickListeners(v: View) {
        v.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            println(checkedId)
            when (checkedId) {
                R.id.ChipSchedule -> {
                    showStandings = false
                    viewModel.getSchedule()
                    removeAllItemDecorations(gamesRv)
                    addItemDecorationToRecyclerView(
                        gamesRv,
                        R.dimen.default_padding,
                        R.dimen.default_padding,
                        R.dimen.default_padding,
                        R.dimen.default_padding_bottom
                    )
                }


                R.id.ChipNextGames -> {
                    showStandings = false
                    viewModel.getNextGames()
                    removeAllItemDecorations(gamesRv)
                    addItemDecorationToRecyclerView(
                        gamesRv,
                        R.dimen.default_padding,
                        R.dimen.default_padding,
                        R.dimen.default_padding,
                        R.dimen.default_padding_bottom
                    )
                }


                R.id.ChipStandings -> {
                    showStandings = true
                    gamesRv.setHasFixedSize(false)
                    gamesRv.adapter =
                        TeamStandingsRvAdapter(
                            standingsList,
                            context!!
                        )
                    removeAllItemDecorations(gamesRv)
                    addItemDecorationToRecyclerView(
                        gamesRv,
                        R.dimen.default_padding_standings,
                        R.dimen.default_padding_standings
                    )
                    println("STANDINGS DISPLAYED")
                    runLayoutAnimation(gamesRv)
                }
            }
        }
    }

    private fun removeAllItemDecorations(recyclerView: RecyclerView) {
        while (recyclerView.itemDecorationCount > 0) {
            recyclerView.removeItemDecorationAt(0)
        }
    }

    private fun addItemDecorationToRecyclerView(
        recyclerView: RecyclerView,
        dimensionLeft: Int,
        dimensionTop: Int,
        dimensionRight: Int = dimensionLeft,
        dimensionBottom: Int = dimensionTop


    ) {
        recyclerView.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(dimensionTop).toInt(),
                resources.getDimension(dimensionBottom).toInt(),
                resources.getDimension(dimensionLeft).toInt(),
                resources.getDimension(dimensionRight).toInt()

            )
        )
    }
}

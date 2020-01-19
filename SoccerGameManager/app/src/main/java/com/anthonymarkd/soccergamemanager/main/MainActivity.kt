package com.anthonymarkd.soccergamemanager.main

import android.animation.Animator
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import androidx.core.view.GravityCompat
import com.anthonymarkd.soccergamemanager.soccer.SoccerDivisionFragment
import kotlinx.android.synthetic.main.main_activity.*
import android.view.MenuItem
import android.view.View
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import com.anthonymarkd.soccergamemanager.Division
import com.anthonymarkd.soccergamemanager.R
import com.anthonymarkd.soccergamemanager.launch.error.LaunchErrorFragment
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.main_activity.loading_anim


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var viewModel: MainViewModel
    private lateinit var divisons: List<Division>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this /* Activity context */)
        val darkTheme = sharedPreferences.getBoolean("theme_preference_dark", true)
        if (darkTheme) {
            setTheme(R.style.AppThemeDark)
            window.decorView.systemUiVisibility = 0
        } else {
            setTheme(R.style.AppThemeLight)
        }
        setContentView(R.layout.main_activity)
        nvView.setBackgroundColor(
            getColorFromAttr(R.attr.colorSurface)
        )
        nvView.setNavigationItemSelectedListener(this)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        val divisionsObserver = Observer<List<Division>> { newDivisions ->
            // Update the UI
            // Access the RecyclerView Adapter and load the data into it

            divisons = newDivisions
            println("Size of divisions : " + newDivisions.size)
            newDivisions.forEachIndexed { index, division ->
                nvView.menu.add(R.id.menu_top, index, index, division.divisionName)
            }
            nvView.menu.setGroupCheckable(R.id.menu_top, true, true)
            val selectedDivision = savedInstanceState?.getInt("selectedDivision", 0) ?: 0
            nvView.setCheckedItem(selectedDivision)
            val fragment = supportFragmentManager.findFragmentById(R.id.container)
            if (divisons.isNullOrEmpty()) {
                if (fragment !is LaunchErrorFragment) {
                    viewModel.launchDataLoad()
                }
            } else {
                if (fragment == null) {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.container,
                            SoccerDivisionFragment.newInstance(
                                divisons[0].url,
                                divisons[0].divisionName
                            )
                        )
                        .commitNow()
                }
            }

        }
        val loadingState = Observer<Boolean> { loading ->
            if (loading) {
                loading_anim.visibility = View.VISIBLE
            } else {
                loading_anim.addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                        // Log.e("Animation:", "start")
                    }

                    override fun onAnimationEnd(animation: Animator) {


                    }

                    override fun onAnimationCancel(animation: Animator) {
                        //  Log.e("Animation:", "cancel")
                    }

                    override fun onAnimationRepeat(animation: Animator) {

                        loading_anim.visibility = View.GONE
                        supportFragmentManager.beginTransaction()
                            .replace(
                                R.id.container,
                                SoccerDivisionFragment.newInstance(
                                    divisons[0].url,
                                    divisons[0].divisionName
                                )
                            )
                            .commitNow()
                    }
                })

            }
        }
        viewModel.loading.observe(this, loadingState)
        viewModel.divisions.observe(this, divisionsObserver)
        viewModel.errorState.observe(this, Observer<Boolean> { errorState ->
            errorState?.let {
                if (errorState) {
                    viewModel.errorState.value = false
                    val fragment = supportFragmentManager.findFragmentById(R.id.container)
                    if (fragment !is LaunchErrorFragment) {
                        supportFragmentManager.beginTransaction()
                            .replace(
                                R.id.container,
                                LaunchErrorFragment.newInstance()
                            )
                            .commitNow()
                    }
                }
            }
        })

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val selectedDivision = nvView.checkedItem?.itemId ?: 0
        println(selectedDivision)
        outState.putInt("selectedDivision", selectedDivision)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // The action bar home/up action should open or close the drawer.

        when (item.itemId) {

            android.R.id.home -> {

                navigation_drawer.openDrawer(GravityCompat.START)

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        navigation_drawer.closeDrawer(GravityCompat.START)
        when (item.itemId) {
            else -> {

                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.container,
                        SoccerDivisionFragment.newInstance(
                            divisons[item.itemId].url,
                            divisons[item.itemId].divisionName
                        )
                    )
                    .commitNow()
            }
        }

        return true
    }

    override fun onBackPressed() {
        if (navigation_drawer.isDrawerOpen(GravityCompat.START)) {
            navigation_drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun Context.getColorFromAttr(
        @AttrRes attrColor: Int,
        typedValue: TypedValue = TypedValue(),
        resolveRefs: Boolean = true
    ): Int {
        theme.resolveAttribute(attrColor, typedValue, resolveRefs)
        return typedValue.data
    }
}

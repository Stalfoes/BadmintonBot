package com.anthonymarkd.badmintontrainingbot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_stats.view.*

class StatsFragment : Fragment() {
    private lateinit var viewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_stats, container, false)
        activity?.let {
            viewModel = ViewModelProviders.of(it).get(MainViewModel::class.java)
        }

        (activity as AppCompatActivity).setSupportActionBar(view.toolbar)
        if ((activity as AppCompatActivity).supportActionBar != null) {
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
            view.toolbar.setTitleTextAppearance(context, R.style.AppTheme)

        }
        viewModel.scoreList.observe(this, Observer<List<HitDetection>> { scores ->
            var count = 0
           for (score in scores){
              count += score.score
           }
            view.textView3.text = "Hit: $count"
        })
        return view
    }
}
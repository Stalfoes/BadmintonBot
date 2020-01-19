package com.anthonymarkd.badmintontrainingbot

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.alert_dialog_edittext.*
import kotlinx.android.synthetic.main.fragment_training_pattern.view.*
import java.lang.Exception


class TrainingPatternFragment : Fragment() {
    var points = mutableListOf<Point>()
    private lateinit var viewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_training_pattern, container, false)
        activity?.let {
            viewModel = ViewModelProviders.of(it).get(MainViewModel::class.java)
        }
        view.imageView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val x = event.x.toInt()
                val y = event.y.toInt()
                getShotsAmount(x.toFloat(), y.toFloat())
                println("Coor: X $x , Coor: y $y")
            }
            false
        }

        view.reset_bt.setOnClickListener {
            view.imageView.resetPoints()
            view.imageView.invalidate()
        }
        view.delete_bt.setOnClickListener {
            view.imageView.deleteLastPointEntry()
            view.imageView.invalidate()
        }
        view.next_bt.setOnClickListener {
            if (points.size > 0) {
                viewModel.send("points:" + points.size + "\n")
                for (point in points) {
                    println("Printin Point To Serial")
                    viewModel.send("coord:\n")
                    viewModel.send("x:" + point.x)
                    viewModel.send("y:" + point.y)
                    viewModel.send("shots:" + point.shotsToBeTaken + "\n")
                }
                viewModel.send("end:\n")

                activity!!.supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    StatsFragment()
                )
                .commitNow()
            } else {
                Toast.makeText(this.context, "Please enter some coordinates", Toast.LENGTH_SHORT)
                    .show()
            }

        }
        return view
    }

    fun getShotsAmount(x: Float, y: Float) {
        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle("How many Shots?")
            .setView(R.layout.alert_dialog_edittext)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok") { dialogInterface, which ->

            try {
                val shots = dialog.shots_et.text.toString().toInt()
                updateImageView(x, y, shots)
            } catch (e: Exception) {
                Toast.makeText(this.context, "Please Enter a number", Toast.LENGTH_SHORT).show()
            }

        }
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(resources.getColor(android.R.color.white, null))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(resources.getColor(android.R.color.white, null))
        }
        dialog.show()

    }

    private fun updateImageView(x: Float, y: Float, shots: Int) {
        println(shots)
        points.add(Point(x, y, shots))
        view!!.imageView.setPinArray(points)
        view!!.imageView.invalidate()
    }

}
package com.anthonymarkd.badmintontrainingbot

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_bluetooth.view.*

class BlueToothFragment : Fragment() {
    private lateinit var viewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bluetooth, container, false)
        Log.i("[BLUETOOTH]", "Creating listeners")
        activity?.let {
            viewModel = ViewModelProviders.of(it).get(MainViewModel::class.java)
        }
        viewModel.initBluetooth()
        viewModel.blueToothConnection.observe(this, Observer<Boolean> { bluetoothState ->
            if (bluetoothState) {
                activity!!.supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.container,
                        TrainingPatternFragment()
                    )
                    .commitNow()
            }
        })
        viewModel.blueToothEnable.observe(this, Observer<Boolean> { blueToothEnable ->
            if (!blueToothEnable) {
                val enableBTIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                println("BLUETOOTH NOT ENABLED")
                activity?.startActivityForResult(enableBTIntent, 1)

            }
        })
        view.retryConnecting.setOnClickListener {
            viewModel.initBluetooth()

        }
        view.sendData.setOnClickListener {
            viewModel.send("connected?\n")
//            activity!!.supportFragmentManager.beginTransaction()
//                .replace(
//                    R.id.container,
//                    TrainingPatternFragment()
//                )
//                .commitNow()
        }


        return view

    }
}

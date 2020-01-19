package com.anthonymarkd.badmintontrainingbot

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class MainViewModel : ViewModel() {
    var device: BluetoothDevice? = null
    private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    val macAddress = "00:14:03:06:2C:40"
    val REQUEST_ENABLE_BT = 1
    var blueToothConnection = MutableLiveData<Boolean>()
    var blueToothEnable = MutableLiveData<Boolean>()

    var bta: BluetoothAdapter? = null
    var mmSocket: BluetoothSocket? = null
    var mmDevice: BluetoothDevice? = null
    var btt: ConnectedThread? = null
    var mHandler: Handler? = null
    var scoreList = MutableLiveData<List<HitDetection>>()
    var scores = mutableListOf<HitDetection>()
    init {
        blueToothConnection.value = false
        blueToothEnable.value = false
    }

    fun initBluetooth() {
        viewModelScope.launch {
            bta = BluetoothAdapter.getDefaultAdapter()
            //if bluetooth is not enabled then create Intent for user to turn it on
            if (bta!!.isEnabled) {
                blueToothEnable.value = false
            } else {
                initiateBluetoothProcess()
            }
        }
    }


    fun initiateBluetoothProcess() {
        if (bta!!.isEnabled) { //attempt to connect to bluetooth module
            var tmp: BluetoothSocket? = null
            mmDevice = bta!!.getRemoteDevice(macAddress)
            //create socket
            try {
                if (mmSocket == null) {

                    tmp = mmDevice!!.createRfcommSocketToServiceRecord(MY_UUID)
                    mmSocket = tmp
                    mmSocket!!.connect()
                    Log.i("[BLUETOOTH]", "Connected to: " + mmDevice!!.name)
                    send("connected?\n")
                } else if (!mmSocket!!.isConnected) {
                    tmp = mmDevice!!.createRfcommSocketToServiceRecord(MY_UUID)
                    mmSocket = tmp
                    mmSocket!!.connect()
                    send("connected?\n")
                    Log.i("[BLUETOOTH]", "Connected to: " + mmDevice!!.name)
                } else {
                    println("We are connected already")
                }
            } catch (e: Exception) {
                try {
                    mmSocket!!.close()
                } catch (c: Exception) {
                    println(c)
                }
            }
            Log.i("[BLUETOOTH]", "Creating handler")
            mHandler = object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) { //super.handleMessage(msg);
                    if (msg.what === ConnectedThread.RESPONSE_MESSAGE) {
                        val txt = msg.obj as String
                        println(txt)
                        if (txt == "connected") {
                            println("TEST")
                            blueToothConnection.value = true
                        } else if (txt.contains("p1:")) {
                            val score  = txt.replace("p:", "")
                            val hit = HitDetection("Location",score.toInt())
                            println(hit.score)
                            scores.add(hit)
                            scoreList.value = scores
                        }
                    }
                }
            }
            Log.i("[BLUETOOTH]", "Creating and running Thread")
            btt = ConnectedThread(mmSocket!!, mHandler!!)
            btt!!.start()
            send("connected?\n")
        }
    }

    fun send(string: String) {
        Log.i("[BLUETOOTH]", "Attempting to send data")
        if (mmSocket!!.isConnected && btt != null) { //if we have connection to the bluetoothmodule
            val sendtxt = string
            btt!!.write(sendtxt.toByteArray())

        } else {
            // Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
        }
    }
}
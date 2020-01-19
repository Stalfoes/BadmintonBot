package com.anthonymarkd.soccergamemanager

import androidx.lifecycle.ViewModel
import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.anthonymarkd.soccergamemanager.soccer.SoccerDivisionViewModel


class ViewModelFactory(private val mParam: String,private val application: Application) :
    ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.cast(
            SoccerDivisionViewModel(
                mParam,
                application
            )
        )!!
    }
}
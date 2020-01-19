package com.anthonymarkd.soccergamemanager

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Division (val divisionName: String?, val url: String?){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
package com.anthonymarkd.soccergamemanager

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Division::class,GameDay::class,TeamStanding::class), version = 1, exportSchema = false)
abstract class SoccerDatabase : RoomDatabase() {

    abstract fun divsionDao(): DivisionDao
    abstract fun gameDayDao(): GameDayDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: SoccerDatabase? = null

        fun getDatabase(context: Context): SoccerDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SoccerDatabase::class.java,
                    "soccer_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
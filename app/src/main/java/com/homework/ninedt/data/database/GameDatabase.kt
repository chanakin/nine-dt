package com.homework.ninedt.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.homework.ninedt.data.model.Game
import com.homework.ninedt.data.utils.ioThread

@Database(entities = [Game::class], version = 1)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao

    companion object {
        const val GAME_DB_NAME = "game-db"

        @Volatile
        private var instance: GameDatabase? = null

        fun getInstance(context: Context): GameDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): GameDatabase {

            return Room.databaseBuilder(context, GameDatabase::class.java, GAME_DB_NAME)
                .addCallback(
                    object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Initializes the database with the first game
                            ioThread {
                                getInstance(context).gameDao().createNewGame(Game())
                            }
                        }
                    }
                ).build()
        }
    }
}
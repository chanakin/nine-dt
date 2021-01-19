package com.homework.ninedt.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.homework.ninedt.data.model.Game

@Database(entities = [Game::class], version = 1)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao

    companion object {
        const val GAME_DB_NAME = "game-db"
    }
}
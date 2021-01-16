package com.homework.ninedt.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.homework.ninedt.data.model.Game

@Dao
interface GameDao {

    @Insert
    fun createNewGame(game: Game)

    @Update
    // Returns number of rows updated
    fun updateGame(game: Game)

    @Delete
    // Returns number of rows deleted
    fun deleteGame(game: Game)

    @Query("SELECT * FROM game")
    fun getAllGames(): LiveData<List<Game>>

    @Query("SELECT * FROM game where id = :id")
    fun loadGame(id: Long): LiveData<Game>

    @Query("SELECT * from game where active = 1")
    fun loadActiveGame(): LiveData<Game>
}
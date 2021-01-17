package com.homework.ninedt.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.homework.ninedt.data.model.Game

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
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

    @Query("SELECT * FROM game WHERE status = 'INPROGRESS' ORDER BY createdDate DESC LIMIT 1")
    fun loadActiveGame(): LiveData<Game>

    @Query("SELECT EXISTS(SELECT * from game where status = 'INPROGRESS')")
    fun hasActiveGame(): LiveData<Boolean>
}
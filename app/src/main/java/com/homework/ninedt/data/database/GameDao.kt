package com.homework.ninedt.data.database

import androidx.room.*
import com.homework.ninedt.data.model.Game
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun createNewGame(game: Game): Long

    @Update
    // Returns number of rows updated
    fun updateGame(game: Game): Int

    @Delete
    // Returns number of rows deleted
    fun deleteGame(game: Game): Int

    @Query("SELECT * FROM game")
    fun getAllGames(): Flow<List<Game>>

    @Query("SELECT * FROM game where id = :id")
    fun loadGame(id: Long): Flow<Game>

    @Query("SELECT * FROM game ORDER BY lastModified DESC LIMIT 1")
    fun loadLatestGame(): Flow<Game>

//    @Query("SELECT EXISTS(SELECT * from game where status = 'INPROGRESS')")
//    suspend fun hasActiveGame(): Flow<Boolean>
}
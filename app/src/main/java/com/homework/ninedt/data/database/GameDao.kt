package com.homework.ninedt.data.database

import androidx.room.*
import com.homework.ninedt.data.model.Game
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createNewGame(game: Game)

    @Update
    // Returns number of rows updated
    suspend fun updateGame(game: Game)

    @Delete
    // Returns number of rows deleted
    suspend fun deleteGame(game: Game)

    @Query("SELECT * FROM game")
    suspend fun getAllGames(): Flow<List<Game>>

    @Query("SELECT * FROM game where id = :id")
    suspend fun loadGame(id: Long): Flow<Game>

    @Query("SELECT * FROM game ORDER BY lastModified DESC LIMIT 1")
    fun loadLatestGame(): Flow<Game>

//    @Query("SELECT EXISTS(SELECT * from game where status = 'INPROGRESS')")
//    suspend fun hasActiveGame(): Flow<Boolean>
}
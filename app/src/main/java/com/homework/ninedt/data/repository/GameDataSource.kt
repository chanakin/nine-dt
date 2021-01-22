package com.homework.ninedt.data.repository

import com.homework.ninedt.data.model.Game
import kotlinx.coroutines.flow.Flow

// In MVVM architecture, having a repository standing between the ViewModel
// and data retrieval methods allows the use of local storage as well as API calls,
// allowing us to intelligently balance freshness of data with responsiveness of display
interface GameDataSource {
    fun getGame(id: Long): Flow<Game?>

    fun getLastModifiedGameId(): Flow<Long?>

    suspend fun updateGame(game: Game)

    suspend fun createGame(game: Game): Long

    suspend fun getOtherPlayerMove(game: Game)

    suspend fun saveDroppedToken(game: Game, column: Int)
}


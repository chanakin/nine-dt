package com.homework.ninedt.data.repository

import com.homework.ninedt.data.api.Response
import com.homework.ninedt.data.model.Game
import kotlinx.coroutines.flow.Flow

interface GameDataSource {
    fun getGame(id: Long): Flow<Game?>

    fun getLastModifiedGameId(): Flow<Long?>

    suspend fun updateGame(game: Game): Int

    suspend fun createGame(playerOneId: Long, playerTwoId: Long): Long

    suspend fun startGame(game: Game, currentPlayerId: Long): Response<Game>

    suspend fun makeMove(columnDropped: Int, game: Game): Response<Game>

    suspend fun changeStartingPlayer(game: Game, startingPlayerId: Long): Response<Game>
}


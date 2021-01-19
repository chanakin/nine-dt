package com.homework.ninedt.data.repository

import com.homework.ninedt.data.api.NineDTApiService
import com.homework.ninedt.data.database.GameDao
import com.homework.ninedt.data.model.Game
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

// In MVVM architecture, having a repository standing between the ViewModel
// and data retrieval methods allows the use of local storage as well as API calls,
// allowing us to intelligently balance freshness of data with responsiveness of display
@Singleton
class GameRepository @Inject constructor(
    private val gameDao: GameDao,
    private val gameService: NineDTApiService
) : GameDataSource {
    override fun loadActiveGame(): Flow<Game?> {
        return gameDao.loadLatestGame()
    }

    // TODO when it's time, implement this so they can start a new game
    override suspend fun createGame(game: Game) {
        gameDao.createNewGame(game)
    }

    override suspend fun updateGame(game: Game) {
        game.lastModified = Date()
        gameDao.updateGame(game)
    }

    override suspend fun getOtherPlayerMove(game: Game) {
        game.moves = gameService.getNextMove(game.moves)
        updateGame(game)
    }

    override suspend fun saveDroppedToken(game: Game, column: Int) {
        val newMoves = game.moves.toMutableList()
        newMoves.add(column)
        game.moves = newMoves.toTypedArray()
        updateGame(game)
        game.moves = gameService.getNextMove(newMoves.toTypedArray())
        updateGame(game)
    }
}


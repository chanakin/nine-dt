package com.homework.ninedt.data.repository

import android.util.Log
import com.homework.ninedt.data.api.NineDTApiService
import com.homework.ninedt.data.database.GameDao
import com.homework.ninedt.data.model.Game
import kotlinx.coroutines.flow.*
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

    override fun getGame(id: Long): Flow<Game?> = gameDao.getGame(id)

    override fun getLastModifiedGameId(): Flow<Long?> = gameDao.getLastModifiedGameId()

    override suspend fun createGame(game: Game): Long {
        Log.i(TAG, "Creating a new game $game")
        return gameDao.createNewGame(game)
    }

    override suspend fun updateGame(game: Game) {
        Log.i(TAG, "Updating game $game")
        game.lastModified = Date()
        val updatedRow = gameDao.updateGame(game)
        Log.i(TAG, "Updated row count $updatedRow")
    }

    override suspend fun getOtherPlayerMove(game: Game) {
        Log.i(TAG, "Sending moves: $game.moves")
        game.moves = gameService.getNextMove(
            game.moves.joinToString(
                prefix = "[",
                postfix = "]",
                separator = ","
            )
        )
        Log.i(TAG, "Got moves back: ${game.moves}")
        updateGame(game)
    }

    override suspend fun saveDroppedToken(game: Game, column: Int) {
        Log.i(TAG, "Saving dropped token for $game at column $column")

        updateGame(game)
    }

    companion object {
        const val TAG = "GameRepository"
    }
}


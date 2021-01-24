package com.homework.ninedt.data.repository

import android.util.Log
import com.homework.ninedt.data.api.Response
import com.homework.ninedt.data.api.Status
import com.homework.ninedt.data.database.GameDao
import com.homework.ninedt.data.model.Game
import com.homework.ninedt.data.model.RulesService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

// In MVVM architecture, having a repository standing between the ViewModel
// and data retrieval methods allows the use of local storage as well as API calls,
// allowing us to intelligently balance freshness of data with responsiveness of display
@Singleton
class GameRepository @Inject constructor(
    private val gameDao: GameDao,
    private val rulesService: RulesService
) : GameDataSource {

    override fun getGame(id: Long): Flow<Game?> = gameDao.getGame(id)

    override fun getLastModifiedGameId(): Flow<Long?> = gameDao.getLastModifiedGameId()

    override suspend fun createGame(): Long {
        return gameDao.createNewGame(Game())
    }

    override suspend fun updateGame(game: Game): Int {
        Log.i(TAG, "Updating game $game")
        game.lastModified = Date()
        return gameDao.updateGame(game)
    }

    override suspend fun startGame(game: Game, currentPlayerId: Long): Response<Game> {
        return withContext(Dispatchers.IO) {
            val response = rulesService.startGame(game, currentPlayerId)

            if (response.status == Status.SUCCESS) {
                updateGame(response.data!!)
            }

            return@withContext response
        }
    }

    override suspend fun makeMove(columnDropped: Int, game: Game): Response<Game> {
        return withContext(Dispatchers.IO) {
            val response = rulesService.makeMove(columnDropped, game)
            Log.i(TAG, "Response received after making move: $response")

            if (response.status == Status.SUCCESS) {
                updateGame(response.data!!)
            }

            return@withContext Response.success(response.data!!)
        }
    }

    override suspend fun changeStartingPlayer(game: Game, startingPlayerId: Long): Response<Game> {
        return withContext(Dispatchers.IO) {
            val response = rulesService.changeStartingPlayer(game, startingPlayerId)

            if (response.status == Status.SUCCESS) {
                updateGame(response.data!!)
            }

            return@withContext response
        }
    }


    companion object {
        const val TAG = "GameRepository"
    }
}


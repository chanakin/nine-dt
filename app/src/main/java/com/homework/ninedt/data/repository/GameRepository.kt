package com.homework.ninedt.data.repository

import com.homework.ninedt.data.api.Response
import com.homework.ninedt.data.api.RulesService
import com.homework.ninedt.data.api.Status
import com.homework.ninedt.data.database.GameDao
import com.homework.ninedt.data.model.Game
import com.homework.ninedt.data.model.GameStatus
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

    override suspend fun createGame(playerOneId: Long, playerTwoId: Long): Long {
        return withContext(Dispatchers.IO) {
            return@withContext gameDao.createNewGame(
                Game(
                    playerOneId = playerOneId,
                    playerTwoId = playerTwoId
                )
            )
        }
    }

    override suspend fun updateGame(game: Game): Int {
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
            var response = rulesService.makeMove(columnDropped, game)

            // This is a little weird, but we're simulating additional trips to the server
            // so that the user gets clear signals when it is their turn vs the computer
            if (response.status == Status.SUCCESS) {
                response.data?.let {
                    updateGame(it)

                    if (it.status == GameStatus.INPROGRESS) {
                        response = rulesService.getNextMove(game)

                        if (response.status == Status.SUCCESS) {
                            updateGame(response.data!!)
                        }
                    }
                }
            }

            return@withContext response
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


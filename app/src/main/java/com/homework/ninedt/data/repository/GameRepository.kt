package com.homework.ninedt.data.repository

import com.homework.ninedt.data.api.NetworkService
import com.homework.ninedt.data.database.GameDao
import com.homework.ninedt.data.model.Game
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

// In MVVM architecture, having a repository standing between the ViewModel
// and data retrieval methods allows the use of local storage as well as API calls,
// allowing us to intelligently balance freshness of data with responsiveness of display
@Singleton
class GameRepository @Inject constructor(
    private val gameDao: GameDao,
    private val gameService: NetworkService
) {
    fun loadActiveGame(): Flow<Game> {
        return gameDao.loadLatestGame()
    }

    // TODO when it's time, implement this so they can start a new game
//    fun createGame(game: Game) {
//        gameDao.createNewGame(game)
//    }

    suspend fun updateGame(game: Game) {
        gameDao.updateGame(game)
    }

    suspend fun getNextMove(game: Game): Game {
        // TODO make API call here instead of mocking a response
        val newMoves = game.moves.toMutableList()
        newMoves.add(Random.nextInt(4))
        game.moves = gameService.nineDTMovesService.getNextMove(newMoves.toTypedArray())
        return game
    }

    //        return object : NetworkBoundResource<Game, Array<Int>>(appExecutors) {
//            override fun saveCallResult(item: Array<Int>) {
//                game.moves = item
//                gameDao.updateGame(game)
//            }
//
//            override fun shouldFetch(data: Game?): Boolean {
//                return true
//            }
//
//            override fun loadFromDb(): LiveData<Game> {
//                // Theoretically this should not be called
//                return gameDao.loadGame(game.id)
//            }
//
//            override fun createCall(): LiveData<ApiResponse<Array<Int>>> {
//                return gameService.getPlayer2Move(game.moves)
//            }
//        }.asLiveData()
    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: GameRepository? = null

        fun getInstance(gameDao: GameDao, gameService: NetworkService) =
            instance ?: synchronized(this) {
                instance ?: GameRepository(gameDao, gameService).also { instance = it }
            }
    }
}


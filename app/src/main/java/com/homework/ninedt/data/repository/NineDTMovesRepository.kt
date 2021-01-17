package com.homework.ninedt.data.repository

import androidx.lifecycle.LiveData
import com.homework.ninedt.AppExecutors
import com.homework.ninedt.data.api.ApiResponse
import com.homework.ninedt.data.api.NineDTMovesService
import com.homework.ninedt.data.api.Resource
import com.homework.ninedt.data.database.GameDao
import com.homework.ninedt.data.model.Game
import javax.inject.Inject
import javax.inject.Singleton

// In MVVM architecture, having a repository standing between the ViewModel
// and data retrieval methods allows the use of local storage as well as API calls,
// allowing us to intelligently balance freshness of data with responsiveness of display
@Singleton
class NineDTMovesRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val gameDao: GameDao,
    private val gameService: NineDTMovesService
) {
    fun loadActiveGame(): LiveData<Game> {
        return gameDao.loadActiveGame()
    }

    fun createGame(game: Game) {
        gameDao.createNewGame(game)
    }

    fun updateGame(game: Game) {
        gameDao.updateGame(game)
    }

    fun getNextMove(game: Game): LiveData<Resource<Game>> {
        return object : NetworkBoundResource<Game, Array<Int>>(appExecutors) {
            override fun saveCallResult(item: Array<Int>) {
                game.moves = item
                gameDao.updateGame(game)
            }

            override fun shouldFetch(data: Game?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<Game> {
                // Theoretically this should not be called
                return gameDao.loadGame(game.id)
            }

            override fun createCall(): LiveData<ApiResponse<Array<Int>>> {
                return gameService.getPlayer2Move(game.moves)
            }
        }.asLiveData()
    }
}
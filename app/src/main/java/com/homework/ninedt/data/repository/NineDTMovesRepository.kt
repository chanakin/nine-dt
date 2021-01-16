package com.homework.ninedt.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.homework.ninedt.data.api.ApiManager
import com.homework.ninedt.data.api.NineDTMovesService
import com.homework.ninedt.data.database.GameDao
import com.homework.ninedt.data.model.Game

// In MVVM architecture, having a repository standing between the ViewModel
// and data retrieval methods allows the use of local storage as well as API calls,
// allowing us to intelligently balance freshness of data with responsiveness of display
class NineDTMovesRepository(private val gameDao: GameDao, private val gameService: NineDTMovesService) {

    fun getGame(): LiveData<Game> {
        val activeGame = gameDao.loadActiveGame()

        if (activeGame == null) {
            activeGame = gameDao.createNewGame(Game(moves = emptyArray(), active = true))
        }

        if (activeGame != null) {
            return LiveData(activeGame)
        }

        if (cachedMoves != null) {
            return liveData { createBoard(cachedMoves) }
        }

        return liveData {  }
    }

    private fun getCachedMoves(): Array<Int>? {
        // TODO
        return null
    }

    suspend fun getPlayer2Move(moves: Array<Int>): Array<Int> {
        return ApiManager.nineDTMovesService.getPlayer2Move(moves)
    }
}
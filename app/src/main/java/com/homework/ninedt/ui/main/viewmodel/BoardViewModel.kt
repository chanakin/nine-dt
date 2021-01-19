package com.homework.ninedt.ui.main.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.homework.ninedt.data.model.Game
import com.homework.ninedt.data.repository.GameRepository
import java.util.*
import androidx.hilt.lifecycle.ViewModelInject

class BoardViewModel @ViewModelInject constructor(
    application: Application,
    repository: GameRepository
) : AndroidViewModel(application) {
    val game: LiveData<Game> = repository.loadActiveGame().asLiveData()

    val startingPlayer: LiveData<Int> = Transformations.map(game) { game.value?.startingPlayer}

    val currentPlayer: LiveData<Int> = Transformations.map(game) {
        if (it == null) {
            return@map 0
        }

        if (it.startingPlayer == 0) {
            return@map 0
        }

        val secondPlayer = if (it.startingPlayer == 1) 2 else 1
        return@map if (it.moves.size % 2 == 0) it.startingPlayer else secondPlayer
    }

//    fun setStartingPlayer(startPlayer: Int) {
//        val withStartingPlayer = game.value
//        withStartingPlayer.startingPlayer = startPlayer
//        repository.updateGame()
//        game.value.startingPlayer = startPlayer
//        repository.updateGame()
//        savedStateHandle.set(STARTING_PLAYER_KEY, startPlayer)
//    }

    // I don't love using a hardcoded integer for starting player where 1 is the current user and 2 is the AI.
    // My preference would be to use a User object instead,
    // so that we can designate the winner/loser/current player etc by ID rather than by a magic number
//    fun startGame() {
//        val currentStartingPlayer = startingPlayer.value
//
//        if (currentStartingPlayer != 1 && currentStartingPlayer != 2) {
//            return
//        }
//
//        val createdDate = Date()
//        val newGame = Game(
//            moves = emptyArray(),
//            startingPlayer = currentStartingPlayer, status = GameStatus.INPROGRESS, createdDate = createdDate, lastModified = createdDate)
////        repository.createGame(game = newGame)
//    }

//    fun getBoard(): LiveData<List<MutableList<Char>>> {
//        val moves: LiveData<Array<Int>> = savedStateHandle.getLiveData(MOVES_SS_KEY)
//    }
//
//    fun makeMove(column: Int): LiveData<List<MutableList<Char>>> {
//        return LiveData<>()
//        // Good form here would actually be to NOT implement the logic client side to validate
//        // a move.
////        emit(com.homework.ninedt.data.api.Resource.Loading(data = null))
////
////        try {
////            emit(Result.success(data = repository.getPlayer2Move()))
////        } catch (exception: Exception) {
////            emit(Result.error(data = null, message = exception.message ?: "Error Occurred!"))
////        }
//
//    }

    companion object {
//        const val MOVES_SS_KEY = "moves"
//        const val STARTING_PLAYER_KEY = "startingPlayer"
    }
}
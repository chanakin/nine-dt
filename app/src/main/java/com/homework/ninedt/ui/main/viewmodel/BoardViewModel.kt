package com.homework.ninedt.ui.main.viewmodel

import androidx.lifecycle.*
import com.homework.ninedt.data.model.Game
import com.homework.ninedt.data.repository.GameRepository
import java.util.*
import androidx.hilt.lifecycle.ViewModelInject

class BoardViewModel @ViewModelInject constructor(
    repository: GameRepository
) : ViewModel() {
    val game: LiveData<Game> = repository.loadActiveGame().asLiveData()

    val startingPlayer: LiveData<Int> = Transformations.map(game) { game.value?.startingPlayer}

    val boardGridSize: LiveData<Int> = Transformations.map(game) { game.value?.gridSize }

    val currentPlayer: LiveData<Int> = Transformations.switchMap(game) { game ->
        if (game.startingPlayer == 0) {
            return@switchMap MutableLiveData(0);
        }

        val currentPlayer = MutableLiveData(0)
        val secondPlayer = if (game.startingPlayer == 1) 2 else 1
        currentPlayer.value =  if (game.moves.size % 2 == 0) game.startingPlayer else secondPlayer
        return@switchMap currentPlayer
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

/**
 * Factory for creating a [BoardViewModel] with a constructor that takes a [GameRepository].
 */
class BoardViewModelFactory (
    private val repository: GameRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = BoardViewModel(repository) as T
}

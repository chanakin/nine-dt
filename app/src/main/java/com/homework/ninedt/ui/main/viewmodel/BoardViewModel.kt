package com.homework.ninedt.ui.main.viewmodel

import android.app.Application
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.homework.ninedt.data.model.Game
import com.homework.ninedt.data.model.GameStatus
import com.homework.ninedt.data.repository.GameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class BoardViewModel @ViewModelInject constructor(
    application: Application,
    val repository: GameRepository
) : AndroidViewModel(application) {

    private val gameId: LiveData<Long> =
        repository.getLastModifiedGameId().filterNotNull().distinctUntilChanged().asLiveData()

    val game: LiveData<Game> = Transformations.switchMap(gameId) {
        val flow = repository.getGame(it).filterNotNull()
        Log.i(TAG, "Launching collector of gameflow")
        // Listen for changes that indicate we need to get the next move from the server
        viewModelScope.launch(Dispatchers.IO) {
            flow.collect { gameFromFlow ->
                Log.i(TAG, "Collecting change to game $gameFromFlow")
                if (gameFromFlow.status == GameStatus.INPROGRESS && !isMyTurn(gameFromFlow)) {
                    repository.getOtherPlayerMove(gameFromFlow)
                }
            }
        }

        flow.asLiveData(viewModelScope.coroutineContext)
    }

    val isMyTurn: LiveData<Boolean> = Transformations.map(game) {
        return@map isMyTurn(it)
    }.distinctUntilChanged()

    val board: LiveData<Array<Array<Int>>> =
        Transformations.map(game) {
            Log.i(TAG, "Regenerating board after change in game $it")
            return@map it.createBoard()
        }.distinctUntilChanged()

    private fun isMyTurn(it: Game): Boolean {
        if (it.startingPlayer == null) {
            return false
        }

        val secondPlayer = if (it.startingPlayer == 1) 2 else 1
        val currentPlayer = if (it.moves.size % 2 == 0) it.startingPlayer else secondPlayer

        return currentPlayer == 1
    }

    fun setStartingPlayer(startPlayer: Int) {
        game.value?.let {
            // Reject changes unless we are only in initialized state
            if (it.status == GameStatus.INITIALIZED) {
                Log.i(TAG, "Setting starting player $startPlayer")
                it.startingPlayer = startPlayer
                viewModelScope.launch(Dispatchers.IO) {
                    repository.updateGame(it)
                }
            }
        }
    }

    fun startGame() {
        game.value?.let {
            if (it.status != GameStatus.INITIALIZED) {
                return
            }

            if (it.startingPlayer == null) {
                return
            }

            viewModelScope.launch(Dispatchers.IO) {
                it.status = GameStatus.INPROGRESS
                repository.updateGame(it)
            }
        }
    }

    fun dropToken(column: Int) {
        Log.i(TAG, "Dropped token in column $column")
        game.value?.let { preDropToken ->
            viewModelScope.launch(Dispatchers.IO) {
                val newMoves = preDropToken.moves.toMutableList()
                newMoves.add(column)
                preDropToken.moves = newMoves.toTypedArray()
                repository.updateGame(game = preDropToken)
            }
        }
    }

    companion object {
        const val TAG = "BoardViewModel"
    }
}
package com.homework.ninedt.ui.main.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.homework.ninedt.R
import com.homework.ninedt.data.api.Response
import com.homework.ninedt.data.api.Status
import com.homework.ninedt.data.model.Game
import com.homework.ninedt.data.model.GameStatus
import com.homework.ninedt.data.repository.GameRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class GameViewModel @ViewModelInject constructor(
    application: Application,
    private val repository: GameRepository
) : AndroidViewModel(application) {
    private val myPlayerId = application.getSharedPreferences(
        application.getString(R.string.shared_prefs_file_key),
        Context.MODE_PRIVATE
    ).getLong(application.getString(R.string.player_id), 1L)

    private val computerPlayerId = application.getSharedPreferences(
        application.getString(R.string.shared_prefs_file_key),
        Context.MODE_PRIVATE
    ).getLong(application.getString(R.string.computer_AI_player_id), 2L)

    private val _error = MutableLiveData<String?>()

    private val gameId: LiveData<Long> =
        repository.getLastModifiedGameId().filterNotNull().distinctUntilChanged().asLiveData()

    val game: LiveData<Game> = Transformations.switchMap(gameId) {
        Log.i(TAG, "Game ID changing, triggering new repository update $gameId")
        repository.getGame(it).filterNotNull().asLiveData(viewModelScope.coroutineContext)
    }

    val error: LiveData<String?> = _error

    val isMyTurn: LiveData<Boolean> = Transformations.map(game) {
        return@map isMyTurn(it)
    }.distinctUntilChanged()

    val status: LiveData<GameStatus> =
        Transformations.map(game) {
            return@map it.status
        }.distinctUntilChanged()

    private fun isMyTurn(currentGame: Game): Boolean {
        val currentPlayerId =
            if (currentGame.moves.size % 2 == 0) currentGame.playerOneId else currentGame.playerTwoId
        return currentPlayerId == myPlayerId
    }

    private fun handleResponse(response: Response<Game>) {
        if (response.status == Status.SUCCESS) {
            _error.value = null
        } else {
            _error.value = response.message
        }
    }

    fun setStartingPlayer(startPlayer: Long) {
        game.value?.let {
            viewModelScope.launch {
                Log.i(TAG, "Setting starting player $startPlayer")
                val response = repository.changeStartingPlayer(it, startPlayer)
                handleResponse(response)
            }
        }
    }

    fun createNewGame() {
        viewModelScope.launch {
            repository.createGame(myPlayerId, computerPlayerId)
        }
    }

    fun startGame() {
        game.value?.let {
            viewModelScope.launch {
                val response = repository.startGame(it, currentPlayerId = myPlayerId)
                handleResponse(response)
            }
        }
    }

    fun dropToken(column: Int) {
        Log.i(TAG, "Dropped token in column $column")
        game.value?.let {
            viewModelScope.launch {
                val response = repository.makeMove(column, it)
                handleResponse(response)
            }
        }
    }

    companion object {
        const val TAG = "BoardViewModel"
    }
}
package com.homework.ninedt.ui.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.homework.ninedt.data.repository.NineDTMovesRepository

class BoardViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: NineDTMovesRepository
) : ViewModel() {

    fun getBoard(): LiveData<List<MutableList<Char>>> {
        val moves: LiveData<Array<Int>> = savedStateHandle.getLiveData(MOVES_SS_KEY)
        return createBoard(moves)
    }

    fun makeMove(column: Int): LiveData<List<MutableList<Char>>> {
        // Good form here would actually be to NOT implement the logic client side to validate
        // a move.
        emit(Resource.Loading(data = null))

        try {
            emit(Result.success(data = repository.getPlayer2Move()))
        } catch (exception: Exception) {
            emit(Result.error(data = null, message = exception.message ?: "Error Occurred!"))
        }

    }

    val gridSize: Int = savedStateHandle["gridSize"] ?: 4

    companion object {
        const val MOVES_SS_KEY = "moves"

        private fun createBoard(moves: Array<Int>): List<MutableList<Char>> {
            val board: List<MutableList<Char>> =
                List(4) { MutableList(4, { '-' }) }

            moves.forEachIndexed { index, columnPlaced ->
                run {
                    val playerToken: Char = if (index % 2 == 0) 'R' else 'B'
                    board[index].add(playerToken)
                }
            }

            return board
        }
    }
}
package com.homework.ninedt.data.api

import android.util.Log
import androidx.annotation.VisibleForTesting
import com.homework.ninedt.data.model.Game
import com.homework.ninedt.data.model.GameException
import com.homework.ninedt.data.model.GameStatus
import javax.inject.Inject

// I am separating this out merely because this is truly business logic that belongs on the server,
// not within the client itself
// Yes, I could do extension functions for many of the methods in this class. But for sake of keeping business logic
// out of the Game class and in one "place", I'm forcing it to be a parameter here, rather than
// a collection of extension functions just laying in a file somewhere
class RulesService @Inject constructor(private val gameService: NineDTApiService) {
    suspend fun startGame(game: Game, currentPlayerId: Long): Response<Game> {
        if (game.status != GameStatus.INITIALIZED) {
            // Technically it would be preferable to have these as strings coming out of resources for localization purposes,
            // but I think it is wiser to not be passing around context inside of a class like this, given it's just a "faux" backend
            // service
            return Response.error("This game has already started.", game)
        }

        game.status = GameStatus.INPROGRESS

        if (game.playerOneId != currentPlayerId && game.moves.isEmpty()) {
            // Request the first move from the server.
            return getNextMove(game)
        }

        return Response.success(game)
    }

    fun changeStartingPlayer(game: Game, startingPlayerId: Long): Response<Game> {
        if (game.status == GameStatus.INITIALIZED) {
            if (startingPlayerId == game.playerOneId) {
                return Response.success(game)
            }

            game.playerTwoId = game.playerOneId
            game.playerOneId = startingPlayerId
            return Response.success(game)
        }

        return Response.error("Cannot change the starting player after a game has begun.", game)
    }

    fun makeMove(columnDropped: Int, game: Game): Response<Game> {
        try {
            validateMove(columnDropped, game)
        } catch (ge: GameException) {
            return Response.error(ge.message, game)
        }

        return moveCompleted(game, columnDropped)
    }

    @Throws
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun validateMove(columnDropped: Int, game: Game): Boolean {
        Log.i(TAG, "Validating move at index $columnDropped for $game")
        if (columnDropped < 0 || columnDropped > 3) {
            throw GameException(
                "The column selected is out of bounds for the board.",
                IllegalArgumentException()
            )
        }

        val column = game.board[columnDropped]
        if (!column.contains(null)) {
            throw GameException(
                "The column selected has no empty slots remaining!",
                IllegalArgumentException()
            )
        }

        Log.i(TAG, "Move was found valid.")
        return true
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun checkIfGameIsOver(game: Game): Boolean {
        Log.i(TAG, "Checking for end of game conditions for $game")
        // First check for a win
        if (checkForWin(game)) {
            val lastPlayer = if (game.moves.size % 2 == 1) game.playerOneId else game.playerTwoId
            game.winningPlayerId = lastPlayer
            game.status = GameStatus.COMPLETED
            Log.i(TAG, "A win was achieved!")
            return true
        }

        game.board.forEach { column: Array<Long?> ->
            // check every value
            if (column.contains(null)) {
                return false
            }
        }

        Log.i(TAG, "All slots were filled, but no one won! :(")
        game.status = GameStatus.COMPLETED
        return true
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun checkForWin(game: Game): Boolean {
        // It takes at least 7 moves for a win to be possible
        if (game.moves.size < 7) {
            return false
        }

        val board = game.board
        val lastPlacedColumnIndex = game.moves[game.moves.size - 1]
        val rowIndex = board[lastPlacedColumnIndex].indexOfFirst { it != null }
        val lastPlayedToken =
            board[lastPlacedColumnIndex][rowIndex]!! // guaranteed non-null by the check above

        // Check vertical -- this is the easiest; we only check if we have all 4 tokens in the column
        // set
        if (checkVertical(lastPlayedToken, rowIndex, lastPlacedColumnIndex, board)) {
            return true
        }

        // Check horizontal
        if (checkHorizontal(lastPlayedToken, rowIndex, board)) {
            return true
        }

        // Check diagonal
        if (checkDescendingDiagonal(lastPlayedToken, rowIndex, lastPlacedColumnIndex, board)) {
            return true
        }

        return checkAscendingDiagonal(lastPlayedToken, rowIndex, lastPlacedColumnIndex, board)
    }

    private fun checkVertical(
        lastPlayedToken: Long,
        rowIndex: Int,
        columnIndex: Int,
        board: Array<Array<Long?>>,
    ): Boolean {
        Log.i(TAG, "Checking vertical for tokens $lastPlayedToken in column $columnIndex and row $rowIndex - all column values are ${board[columnIndex]}")
        if (rowIndex == 0 && board[columnIndex].all { tokenPlayed -> tokenPlayed == lastPlayedToken }) {
            Log.i(TAG, "Win found on vertical")
            return true
        }

        return false
    }

    private fun checkHorizontal(playerToken: Long, row: Int, board: Array<Array<Long?>>): Boolean {
        for (column in 0 until Game.GRID_SIZE) {
            Log.i(TAG, "Checking column $column and row $row - token is ${board[column][row]}")
            if (board[column][row] != playerToken) {
                return false
            }
        }

        Log.i(TAG, "Win found on horizontal")

        return true
    }

    private fun checkDescendingDiagonal(
        playerToken: Long,
        rowIndex: Int,
        columnIndex: Int,
        board: Array<Array<Long?>>
    ): Boolean {
        // An optimization - we will take advantage of the fact this is a square grid
        if (rowIndex != columnIndex) {
            return false
        }

        for (i in 0 until Game.GRID_SIZE) {
            // Check: does the token match our last played token
            if (board[i][i] != playerToken) {
                return false
            }
        }

        Log.i(TAG, "Win found on descending diagonal")
        return true
    }

    private fun checkAscendingDiagonal(
        playerToken: Long,
        rowIndex: Int,
        columnIndex: Int,
        board: Array<Array<Long?>>
    ): Boolean {
        // An optimization - again taking advantage of the fact this is a square grid
        // get to the bottom of the grid
        val distanceToBottomRow =
            Game.GRID_SIZE - rowIndex - 1 // you must offset by 1 since grid size is not 0-based

        // Change the coordinates and see if they are our bottom corner coordinates
        if (columnIndex - distanceToBottomRow != 0) {
            return false
        }

        // start at the bottom and work your way up, lads & lassies
        var rowIndexStart = Game.GRID_SIZE - 1

        for (columnIndex in 0 until Game.GRID_SIZE) {
            if (board[columnIndex][rowIndexStart] != playerToken) {
                return false
            }
            rowIndexStart--
        }

        Log.i(TAG, "Win found on ascending diagonal")
        return true
    }

    private fun moveCompleted(game: Game, move: Int): Response<Game> {
        game.addMove(move)
        checkIfGameIsOver(game)
        return Response.success(game)
    }

    suspend fun getNextMove(game: Game): Response<Game> {
        return try {
            val moves = gameService.getNextMove(
                game.moves.joinToString(
                    prefix = "[",
                    postfix = "]",
                    separator = ","
                )
            )

            Log.i(TAG, "After moves received $moves")
            moveCompleted(game, moves[moves.size - 1])
        } catch (e: Exception) {
            Response.error(e.message ?: "Unexpected error received", game)
        }
    }

    companion object {
        const val TAG = "RulesService"
    }
}
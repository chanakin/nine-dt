package com.homework.ninedt.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.homework.ninedt.data.database.DatabaseConverter
import java.util.*

@Entity
@TypeConverters(DatabaseConverter::class)
data class Game(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var moves: Array<Int> = emptyArray(),
    var status: GameStatus = GameStatus.INITIALIZED,
    var startingPlayer: Int? = null,
    val createdDate: Date = Date(),
    var lastModified: Date = Date()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Game

        if (id != other.id) return false
        if (!moves.contentEquals(other.moves)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + moves.contentHashCode()
        return result
    }

    fun awaitingStart(): Boolean {
        return startingPlayer == null || status == GameStatus.INITIALIZED
    }

    fun createBoard(): Array<Array<Int>> {
        // Okay a couple of notes here.
        // We're going to "fake" the board. Since it is a square,
        // we can swap the rows and columns interchangeably - normally
        // the first array represents rows and the second represents columns in a 2D
        // array. In this case, we're going to have the first array represent columns
        // and the second represent rows.
        val board = Array(GRID_SIZE) {
            Array(GRID_SIZE) {
                0
            }
        }

        startingPlayer?.let { start ->
            val secondPlayer = if (start == 1) 2 else 1

            moves.forEachIndexed { index, columnDropped ->
                if (!validateMove(columnDropped, board)) {
                    throw GameException(
                        "Invalid move was encountered; placing a token at column index $columnDropped is outside the bounds of the board.",
                        RuntimeException()
                    )
                }

                val boardColumn = board[columnDropped]
                val nextEmptySlot = boardColumn.indexOfLast { space -> space == 0 }
                val currentPlayer = if (index % 2 == 0) start else secondPlayer
                boardColumn[nextEmptySlot] = currentPlayer
            }
        }

        return board
    }

    fun validateMove(columnDropped: Int, board: Array<Array<Int>>): Boolean {
        if (columnDropped < 0 || columnDropped > 3) {
            return false
        }

        val column = board[columnDropped]
        return column.contains(0) // a slot must be empty or you can't place this
    }

    fun checkForWin(lastPlacedColumnIndex: Int, board: Array<Array<Int>>): Boolean {
        val rowIndex = board[lastPlacedColumnIndex].indexOfFirst { it != 0 }
        val lastPlayedToken = board[lastPlacedColumnIndex][rowIndex]

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
        lastPlayedToken: Int,
        rowIndex: Int,
        columnIndex: Int,
        board: Array<Array<Int>>,
    ): Boolean {
        if (rowIndex == GRID_SIZE && board[columnIndex].none { tokenPlayed -> tokenPlayed != lastPlayedToken }) {
            return true
        }

        return false
    }

    fun checkHorizontal(playerToken: Int, row: Int, board: Array<Array<Int>>): Boolean {
        for (column in 0..GRID_SIZE) {
            if (board[column][row] != playerToken) {
                return false
            }
        }

        return true
    }

    fun checkDescendingDiagonal(
        playerToken: Int,
        rowIndex: Int,
        columnIndex: Int,
        board: Array<Array<Int>>
    ): Boolean {
        // An optimization - we will take advantage of the fact this is a square grid
        if (rowIndex != columnIndex) {
            return false
        }

        for (i in 0..GRID_SIZE) {
            // Check: does the token match our last played token
            if (board[i][i] != playerToken) {
                return false
            }
        }

        return true
    }

    fun checkAscendingDiagonal(
        playerToken: Int,
        rowIndex: Int,
        columnIndex: Int,
        board: Array<Array<Int>>
    ): Boolean {
        // An optimization - again taking advantage of the fact this is a square grid
        // get to the bottom of the grid
        val distanceToBottomRow =
            GRID_SIZE - rowIndex - 1 // you must offset by 1 since grid size is not 0-based

        // Change the coordinates and see if they are our bottom corner coordinates
        if (columnIndex - distanceToBottomRow != 0) {
            return false
        }

        for (column in GRID_SIZE downTo 0) {
            for (row in 0..GRID_SIZE) {
                if (board[column][row] != playerToken) {
                    return false
                }
            }
        }

        return true
    }

    fun winConditionMet(): Boolean {
        // TODO complete this logic
        // Split the array of moves into two
        val p1Moves = mutableListOf<Int>()
        val p2Moves = mutableListOf<Int>()

        moves.forEachIndexed { index, move ->
            if (index % 2 == 0) {
                p1Moves.add(move)
            } else {
                p2Moves.add(move)
            }
        }

        // This will only return true if the column matches
        return p1Moves.groupingBy { it }.eachCount().filterValues { count -> count == 4 }.size > 0
                || p2Moves.groupingBy { it }.eachCount()
            .filterValues { count -> count == 4 }.size > 0
    }

    companion object {
        // Hardcoded for now as dynamic building of a grid is a bigger effort to ensure
        // optimal performance. At least it's not a magic number this way :)
        const val GRID_SIZE: Int = 4
    }
}
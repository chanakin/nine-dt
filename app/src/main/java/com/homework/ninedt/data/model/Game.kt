package com.homework.ninedt.data.model

import android.util.Log
import androidx.room.Entity
import androidx.room.Ignore
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
    var startingPlayerId: Long? = null,
    val playerIds: Array<Long> = emptyArray(),
    val createdDate: Date = Date(),
    var lastModified: Date = Date(),
    var winningPlayerId: Long? = null
) {
    @delegate:Ignore
    val board by lazy {
        createBoard(moves)
    }

    // This assumes that the moves that have been played are valid.
    private fun createBoard(moves: Array<Int>): Array<Array<Long?>> {
        // Okay a couple of notes here.
        // We're going to "fake" the board. Since it is a square,
        // we can swap the rows and columns interchangeably - normally
        // the first array represents rows and the second represents columns in a 2D
        // array. In this case, we're going to have the first array represent columns
        // and the second represent rows.
        val board = Array(GRID_SIZE) {
            Array<Long?>(GRID_SIZE) {
                null
            }
        }

        startingPlayerId?.let { start ->
            val secondPlayer = playerIds.find { playerId -> startingPlayerId != playerId }!!

            moves.forEachIndexed { index, columnDropped ->
                val player = if (index % 2 == 0) start else secondPlayer
                placeTokenOnBoard(board, columnDropped, player)
            }
        }

        return board
    }

    fun addMove(column: Int) {
        Log.i(TAG, "Adding move at column $column")
        val newMoves = moves.toMutableList()
        newMoves.add(column)
        moves = newMoves.toTypedArray()

        // Place onto the board
        startingPlayerId?.let { startingPlayer ->
            val secondPlayer = playerIds.find { playerId -> startingPlayerId != playerId }!!
            val playerId = if (moves.size % 2 == 0) startingPlayer else secondPlayer
            placeTokenOnBoard(board, column, playerId)
        }
    }

    private fun placeTokenOnBoard(
        board: Array<Array<Long?>>,
        columnDropped: Int,
        playerId: Long
    ) {
        val boardColumn = board[columnDropped]
        val nextEmptySlot = boardColumn.indexOfLast { space -> space == null }
        boardColumn[nextEmptySlot] = playerId
    }

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

    companion object {
        // Hardcoded for now as dynamic building of a grid is a bigger effort to ensure
        // optimal performance. At least it's not a magic number this way :)
        const val GRID_SIZE: Int = 4
        const val TAG = "Game"
    }
}
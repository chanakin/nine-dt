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

    fun readyToPlay(): Boolean {
        return startingPlayer != null && when (status) {
            GameStatus.FORFEIT -> false
            GameStatus.CANCELLED -> false
            GameStatus.LOST -> false
            GameStatus.WON -> false
            else -> true
        }
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
                if (columnDropped < 0 || columnDropped > 3) {
                    throw GameException(
                        "Invalid move was encountered; column index $columnDropped is outside the bounds of the board.",
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
                || p2Moves.groupingBy { it }.eachCount().filterValues { count -> count == 4 }.size > 0
    }

    companion object {
        // Hardcoded for now as dynamic building of a grid is a bigger effort to ensure
        // optimal performance. At least it's not a magic number this way :)
        const val GRID_SIZE: Int = 4
    }
}
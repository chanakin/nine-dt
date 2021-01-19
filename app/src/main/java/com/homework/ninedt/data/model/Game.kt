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
    var startingPlayer: Int = 0,
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

    fun readyToPlay() = startingPlayer != 0

    fun createBoard(): List<List<Int>>? {
        if (startingPlayer == 0) {
            return null
        }

        val board: List<MutableList<Int>> =
            List(GRID_SIZE) { MutableList(GRID_SIZE) { 0 } }

        val secondPlayer = if (startingPlayer == 1) 2 else 1

        moves.forEachIndexed { index, columnPlaced ->
            run {
                val currentPlayer = if (index % 2 == 0) startingPlayer else secondPlayer
                board[columnPlaced].add(currentPlayer)
            }
        }

        return board
    }

    companion object {
        // Hardcoded for now as dynamic building of a grid is a bigger effort to ensure
        // optimal performance. At least it's not a magic number this way :)
        const val GRID_SIZE: Int = 4
    }
}
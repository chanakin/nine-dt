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
    val status: GameStatus = GameStatus.INPROGRESS,
    val startingPlayer: Int = 0,
    val createdDate: Date = Date(),
    val lastModified: Date = Date(),
    val gridSize: Int = 4
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
}
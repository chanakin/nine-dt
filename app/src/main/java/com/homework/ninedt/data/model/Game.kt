package com.homework.ninedt.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Game(@PrimaryKey(autoGenerate = true) var id: Long = 0, var moves: Array<Int>, val active: Boolean) {
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
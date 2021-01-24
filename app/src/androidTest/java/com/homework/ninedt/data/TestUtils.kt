package com.homework.ninedt.data

import com.homework.ninedt.data.model.Game
import kotlin.random.Random

fun generateGameForTest(
    moves: Array<Int> = emptyArray(),
    playerOneId: Long = Random.nextLong(),
    playerTwoId: Long = Random.nextLong()
): Game {
    return Game(moves = moves, playerOneId = playerOneId, playerTwoId = playerTwoId)
}
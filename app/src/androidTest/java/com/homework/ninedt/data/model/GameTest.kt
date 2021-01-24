package com.homework.ninedt.data.model

import com.google.gson.Gson
import com.homework.ninedt.data.generateGameForTest
import com.homework.ninedt.data.model.Game
import com.homework.ninedt.data.model.GameException
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.random.Random

@RunWith(JUnit4::class)
class GameTest {

    @Test
    fun emptyMoves_returnsEmptyBoard() {
        // SETUP
        val emptyMoves = emptyArray<Int>()
        val testGame = Game(moves = emptyMoves, playerOneId = 1L, playerTwoId = 2L)

        // assert all of the board's values are 0
        testGame.board.forEach { col ->
            col.forEach { slotValue ->
                assert(slotValue == null)
            }
        }
    }


    @Test()
    fun twoMovesSameColumn_succeedsInCreatingBoard() {
        // SETUP
        val validMoves = arrayOf(3, 3)
        val testGame = generateGameForTest(moves = validMoves, playerOneId = 1L, playerTwoId = 2L)

        testGame.board.forEachIndexed { colIndex, col ->
            col.forEachIndexed { slotIndex, slotValue ->
                if (colIndex == 3) {
                    if (slotIndex == 3) {
                        assert(slotValue == 1L)
                    } else if (slotIndex == 2) {
                        assert(slotValue == 2L)
                    }
                } else {
                    assert(slotValue == null)
                }
            }
        }
    }


    @Test()
    fun twoMovesDifferentColumn_succeedsInCreatingBoard() {
        // SETUP
        val validMoves = arrayOf(3, 2)
        val testGame = generateGameForTest(moves = validMoves, playerOneId = 1L, playerTwoId = 2L)

        testGame.board.forEachIndexed { colIndex, col ->
            col.forEachIndexed { slotIndex, slotValue ->
                if (colIndex == 3 && slotIndex == 3) {
                    assert(slotValue == 1L)
                } else if (colIndex == 2 && slotIndex == 3) {
                    assert(slotValue == 2L)
                } else {
                    assert(slotValue == null)
                }
            }
        }
    }


    @Test()
    fun multipleMovesSameAndDifferentColumns_succeedsInCreatingBoard() {
        // SETUP
        val validMoves = arrayOf(3, 2, 3, 1, 3, 3)
        val testGame = generateGameForTest(moves = validMoves, playerOneId = 1L, playerTwoId = 2L)

        testGame.board.forEachIndexed { colIndex, col ->
            col.forEachIndexed { slotIndex, slotValue ->
                when (colIndex) {
                    3 -> when (slotIndex) {
                        3 -> assert(slotValue == 2L)
                        2 -> assert(slotValue == 2L)
                        1 -> assert(slotValue == 1L)
                        0 -> assert(slotValue == 2L)
                        else -> assert(false, lazyMessage = {
                            "Slot index is bad and should have thrown an exception!"
                        })
                    }
                    2 -> when (slotIndex) {
                        3 -> assert(slotValue == 1L)
                        else -> assert(slotValue == null)
                    }
                    1 -> when (slotIndex) {
                        3 -> assert(slotValue == 1L)
                        else -> assert(slotValue == null)
                    }
                    else -> assert(slotValue == null)
                }
            }
        }
    }
}
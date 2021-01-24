package com.homework.ninedt

import com.homework.ninedt.data.model.Game
import com.homework.ninedt.data.model.GameException
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GameModelTest {

    @Rule
    @JvmField
    val thrown = ExpectedException.none()

    @Test
    fun emptyMoves_returnsEmptyBoard() {
        // SETUP
        val emptyMoves = emptyArray<Int>()
        val testGame = Game(moves = emptyMoves)

        val board = testGame.createBoard()

        // assert all of the board's values are 0
        board.forEach { col ->
            col.forEach { slotValue ->
                assert(slotValue == 0)
            }
        }
    }

    @Test()
    fun moveBeforeZero_throwsGameException() {
        // SETUP
        val badMoves = arrayOf(-1)
        val testGame = Game(moves = badMoves, startingPlayerId = 1)

        thrown.expect(GameException::class.java)
        thrown.expectMessage("Invalid move was encountered; column index -1 is outside the bounds of the board.")

        testGame.createBoard()
    }

    @Test()
    fun moveBeyondThree_throwsGameException() {
        // SETUP
        val badMoves = arrayOf(4)
        val testGame = Game(moves = badMoves, startingPlayerId = 1)

        thrown.expect(GameException::class.java)
        thrown.expectMessage("Invalid move was encountered; column index 4 is outside the bounds of the board.")

        testGame.createBoard()
    }

    @Test()
    fun singleValidMove_succeedsInCreatingBoard() {
        // SETUP
        val validMoves = arrayOf(3)
        val testGame = Game(moves = validMoves, startingPlayerId = 1)
        val board = testGame.createBoard()

        board.forEachIndexed { colIndex, col ->
            col.forEachIndexed { slotIndex, slotValue ->
                if (colIndex == 3 && slotIndex == 3) {
                    assert(slotValue == 1)
                } else {
                    assert(slotValue == 0)
                }
            }
        }
    }

    @Test()
    fun twoMovesSameColumn_succeedsInCreatingBoard() {
        // SETUP
        val validMoves = arrayOf(3, 3)
        val testGame = Game(moves = validMoves, startingPlayerId = 1)
        val board = testGame.createBoard()

        board.forEachIndexed { colIndex, col ->
            col.forEachIndexed { slotIndex, slotValue ->
                if (colIndex == 3) {
                    if (slotIndex == 3) {
                        assert(slotValue == 1)
                    } else if (slotIndex == 2) {
                        assert(slotValue == 2)
                    }
                } else {
                    assert(slotValue == 0)
                }
            }
        }
    }


    @Test()
    fun twoMovesDifferentColumn_succeedsInCreatingBoard() {
        // SETUP
        val validMoves = arrayOf(3, 2)
        val testGame = Game(moves = validMoves, startingPlayerId = 1)
        val board = testGame.createBoard()

        board.forEachIndexed { colIndex, col ->
            col.forEachIndexed { slotIndex, slotValue ->
                if (colIndex == 3 && slotIndex == 3) {
                    assert(slotValue == 1)
                } else if (colIndex == 2 && slotIndex == 3) {
                    assert(slotValue == 2)
                } else {
                    assert(slotValue == 0)
                }
            }
        }
    }


    @Test()
    fun multipleMovesSameAndDifferentColumns_succeedsInCreatingBoard() {
        // SETUP
        val validMoves = arrayOf(3, 2, 3, 1, 3, 3)
        val testGame = Game(moves = validMoves, startingPlayerId = 2)
        val board = testGame.createBoard()

        board.forEachIndexed { colIndex, col ->
            col.forEachIndexed { slotIndex, slotValue ->
                when (colIndex) {
                    3 -> when (slotIndex) {
                        3 -> assert(slotValue == 2)
                        2 -> assert(slotValue == 2)
                        1 -> assert(slotValue == 1)
                        0 -> assert(slotValue == 2)
                        else -> assert(false, lazyMessage = {
                            "Slot index is bad and should have thrown an exception!"
                        })
                    }
                    2 -> when (slotIndex) {
                        3 -> assert(slotValue == 1)
                        else -> assert(slotValue == 0)
                    }
                    1 -> when (slotIndex) {
                        3 -> assert(slotValue == 1)
                        else -> assert(slotValue == 0)
                    }
                    else -> assert(slotValue == 0)
                }
            }
        }
    }

    @Test
    fun tokenDroppedInLeftmostEmptyColumn_isValid() {
        val game = Game()

        assert(game.validateMove(0, game.createBoard()))
    }

    @Test
    fun tokenDroppedInRightmostEmptyColumn_isValid() {
        val game = Game()

        assert(game.validateMove(3, game.createBoard()))
    }

    @Test
    fun tokenDroppedOutsideRightmostColumnBounds_asFirstMove_failsValidation() {
        val game = Game()
        Assert.assertFalse(game.validateMove(4, game.createBoard()))
    }

    @Test
    fun tokenDroppedOutsideLeftMostColumnBounds_asFirstMove_failsValidation() {
        val game = Game()
        Assert.assertFalse(game.validateMove(-1, game.createBoard()))
    }

    @Test
    fun tokenDroppedInFullColumn_failsValidation() {
        val game = Game(moves = arrayOf(1,1,1,1))
        Assert.assertFalse(game.validateMove(1, game.createBoard()))
    }

    @Test
    fun tokenDroppedInLastEmptySpace_isValid() {
        val game = Game(moves = arrayOf(1,1,1))
        assert(game.validateMove(1, game.createBoard()))
    }

    @Test
    fun tokenDroppedInColumnWith2Tokens_isValid() {
        val game = Game(moves = arrayOf(2,2))
        assert(game.validateMove(2, game.createBoard()))
    }

    @Test
    fun tokenDroppedInColumnOnBoardWithOnlyOneSpaceRemaining_isValid() {
        val game = Game(moves = arrayOf(1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4))
        assert(game.validateMove(4, game.createBoard()))
    }
}
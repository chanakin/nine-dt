package com.homework.ninedt.data.api

import com.google.gson.Gson
import com.homework.ninedt.data.generateGameForTest
import com.homework.ninedt.data.model.GameException
import com.homework.ninedt.data.model.GameStatus
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.random.Random

@RunWith(JUnit4::class)
class RulesServiceTest {

    class MockNetworkService : NineDTApiService {
        override suspend fun getNextMove(moves: String): Array<Int> {
            val passedInArray = Gson().fromJson(moves, Array<Int>::class.java)
            val randomMove = Random.nextInt(0, 3)

            val newMoves = arrayListOf(randomMove)
            newMoves.addAll(passedInArray)
            return newMoves.toTypedArray()
        }
    }

    private val service = RulesService(MockNetworkService())

    @Rule
    @JvmField
    val thrown = ExpectedException.none()

    @Test
    fun tokenDroppedInLeftmostEmptyColumn_isValid() {
        val game = generateGameForTest()
        assert(service.validateMove(0, game))
    }

    @Test
    fun tokenDroppedInRightmostEmptyColumn_isValid() {
        val game = generateGameForTest()
        assert(service.validateMove(3, game))
    }

    @Test()
    fun tokenDropped_inMiddleEmptyColumn_isValid() {
        // SETUP
        val testGame = generateGameForTest()
        service.validateMove(2, testGame)
    }

    @Test
    fun tokenDroppedOutsideRightmostColumnBounds_asFirstMove_failsValidation() {
        val game = generateGameForTest()

        thrown.expect(GameException::class.java)
        thrown.expectMessage("The column selected is out of bounds for the board.")

        service.validateMove(4, game)
    }

    @Test
    fun tokenDroppedOutsideLeftMostColumnBounds_asFirstMove_failsValidation() {
        val game = generateGameForTest()

        thrown.expect(GameException::class.java)
        thrown.expectMessage("The column selected is out of bounds for the board.")

        service.validateMove(-1, game)
    }

    @Test
    fun tokenDroppedInFullColumn_failsValidation() {
        val game = generateGameForTest(moves = arrayOf(1, 1, 1, 1))

        thrown.expect(GameException::class.java)
        thrown.expectMessage("The column selected has no empty slots remaining!")

        service.validateMove(1, game)
    }

    @Test
    fun tokenDroppedInLastEmptySpace_isValid() {
        val game = generateGameForTest(moves = arrayOf(1, 1, 1))
        assert(service.validateMove(1, game))
    }

    @Test
    fun tokenDroppedInColumnWith2Tokens_isValid() {
        val game = generateGameForTest(moves = arrayOf(2, 2))
        assert(service.validateMove(2, game))
    }

    @Test
    fun tokenDroppedInColumnOnBoardWithOnlyOneSpaceRemaining_isValid() {
        val game = generateGameForTest(moves = arrayOf(0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3))
        assert(service.validateMove(3, game))
    }

    @Test
            /**
             * R
             * R Y
             * R Y
             * R Y
             */
    fun checkForWin_verticalMatch_returnsWin() {
        val game = generateGameForTest(moves = arrayOf(0, 1, 0, 1, 0, 1, 0))
        val result = service.checkForWin(game)
        Assert.assertTrue(result)
    }

    @Test
            /**
             * Y Y Y
             * R R R R
             */
    fun checkForWin_horizontalMatch_returnsWin() {
        val game = generateGameForTest(moves = arrayOf(0, 0, 1, 1, 2, 2, 3))
        val result = service.checkForWin(game)
        Assert.assertTrue(result)
    }

    @Test
            /**
             * [] empty board
             */
    fun checkForWin_emptyMoveset_returnsNoWin() {
        val game = generateGameForTest()
        val result = service.checkForWin(game)
        Assert.assertFalse(result)
    }

    @Test
            /**
             * Y
             * R Y
             * R R Y
             * R Y R Y
             */
    fun checkForWin_descendingDiagonalMatch_returnsWin() {
        val game = generateGameForTest(moves = arrayOf(0, 1, 0, 3, 2, 2, 1, 1, 0, 0))
        val result = service.checkForWin(game)
        Assert.assertTrue(result)
    }


    @Test
            /**
             *       R
             * R Y R Y
             * R R Y Y
             * R Y R Y
             */
    fun checkForWin_ascendingDiagonalMatch_returnsWin() {
        val game = generateGameForTest(moves = arrayOf(0, 1, 0, 3, 2, 2, 1, 1, 0, 3, 2, 3, 3))
        val result = service.checkForWin(game)
        Assert.assertTrue(result)
    }

    @Test
            /**
             * R R
             * Y Y Y Y
             * R Y R R
             */
    fun checkIfGameIsOver_gameWon_returnsTrueMarksGameCompleteSetsWinningPlayer() {
        val game = generateGameForTest(moves = arrayOf(0, 1, 2, 0, 3, 1, 0, 2, 1, 3))

        val afterCheck = service.checkIfGameIsOver(game)

        Assert.assertEquals(afterCheck, true)
        Assert.assertEquals(game.status, GameStatus.COMPLETED)
        Assert.assertEquals(game.playerTwoId, game.winningPlayerId)
    }

    @Test
            /**
             * R
             * Y
             * R
             * Y
             */
    fun checkIfGameIsOver_incompleteBoardNoWinner_returnsFalse() {
        val game = generateGameForTest(moves = arrayOf(0, 0, 0, 0))
        game.status = GameStatus.INPROGRESS

        val result = service.checkIfGameIsOver(game)
        Assert.assertFalse(result)
        Assert.assertEquals(game.status, GameStatus.INPROGRESS)
    }

    @Test
            /**
             * R Y R Y
             * R Y R Y
             * Y R Y Y
             * R R Y R
             */
    fun checkIfGameIsOver_fullBoardNoWinner_returnsTrueMarksGameAsCompleteAndDoesNotSetWinningPlayer() {
        val game =
            generateGameForTest(moves = arrayOf(0, 0, 1, 2, 1, 2, 3, 3, 0, 1, 0, 1, 2, 2, 3, 3))
        val afterCheck = service.checkIfGameIsOver(game)

        Assert.assertEquals(afterCheck, true)
        Assert.assertEquals(game.status, GameStatus.COMPLETED)
        Assert.assertEquals(null, game.winningPlayerId)
    }

    @Test
    fun changeStartingPlayer_gameInInitializedState_succeeds() {
        val game = generateGameForTest()
        val gameP1Id = game.playerOneId
        val gameP2Id = game.playerTwoId

        val result = service.changeStartingPlayer(game, startingPlayerId = gameP2Id)

        Assert.assertEquals(result.status, Status.SUCCESS)
        Assert.assertEquals(result.data!!.playerOneId, gameP2Id)
        Assert.assertEquals(result.data!!.playerTwoId, gameP1Id)
    }

    @Test
    fun changeStartingPlayer_gameHasStarted_fails() {
        val game = generateGameForTest()
        game.status = GameStatus.INPROGRESS

        val result = service.changeStartingPlayer(game, startingPlayerId = game.playerTwoId)

        Assert.assertEquals(result.status, Status.ERROR)
        Assert.assertEquals(
            result.message,
            "Cannot change the starting player after a game has begun."
        )
    }

    @Test
    fun startGame_gameNotInInitializedState_fails() = runBlocking {
        val game = generateGameForTest()
        game.status = GameStatus.INPROGRESS

        val result = service.startGame(game, game.playerOneId)
        Assert.assertEquals(result.status, Status.ERROR)
        Assert.assertEquals(
            result.message,
            "This game has already started."
        )
    }

    @Test
    fun startGame_currentPlayerIsPlayerOne_setsStatusAndWaitsForPlayersMove() = runBlocking {
        val game = generateGameForTest()

        val result = service.startGame(game, game.playerOneId)

        Assert.assertEquals(result.status, Status.SUCCESS)
        Assert.assertEquals(result.data!!.status, GameStatus.INPROGRESS)
        Assert.assertEquals(result.data!!.moves.size, 0)
    }

    @Test
    fun startGame_currentPlayerIsPlayerTwo_setsStatusAndCallsToServerForOpeningMove() = runBlocking {
        val game = generateGameForTest()

        val result = service.startGame(game, game.playerTwoId)

        Assert.assertEquals(result.status, Status.SUCCESS)
        Assert.assertEquals(result.data!!.status, GameStatus.INPROGRESS)
        Assert.assertEquals(result.data!!.moves.size, 1)
    }

    @Test
    fun makeMove_emptyBoard_validColumn_succeeds() {
        val game = generateGameForTest()
        game.status = GameStatus.INPROGRESS
    }
}
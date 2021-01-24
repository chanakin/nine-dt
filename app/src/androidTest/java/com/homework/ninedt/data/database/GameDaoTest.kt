package com.homework.ninedt

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.homework.ninedt.data.database.GameDao
import com.homework.ninedt.data.database.GameDatabase
import com.homework.ninedt.data.generateGameForTest
import com.homework.ninedt.data.model.Game
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

/**
 * Database tests for Game queries
 */
@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var gameDao: GameDao
    private lateinit var db: GameDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, GameDatabase::class.java
        ).build()
        gameDao = db.gameDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }



    @Test
    fun creatingGameInsertsNewGame() = runBlocking {
        // SETUP
        val game = generateGameForTest(arrayOf(1))
        val id = gameDao.createNewGame(game)
        game.id = id

        // TEST
        val byId = gameDao.getGame(id).first()

        // VERIFY
        assertThat(byId, CoreMatchers.equalTo(game))
        assertThat(gameDao.getAllGames().first().size, CoreMatchers.equalTo(1))
    }

    @Test
    fun createMultipleGames_loadAllSucceeds() = runBlocking {
        // SETUP
        repeat(3) {
            gameDao.createNewGame(generateGameForTest(moves = arrayOf(it)))
        }

        // VERIFY
        assertThat(gameDao.getAllGames().first().size, CoreMatchers.equalTo(3))
    }

    @Test
    fun loadingLatestGame_multipleGamesInDB_returnsLatestModified() = runBlocking {
        var lastIdCreated: Long = 0

        repeat(3) {
            lastIdCreated = gameDao.createNewGame(generateGameForTest(moves = arrayOf(it)))
        }

        val byLatest = gameDao.getGame(gameDao.getLastModifiedGameId().first()).first()
        assertThat(byLatest?.id, CoreMatchers.equalTo(lastIdCreated))
    }

    @Test
    fun updatingGame_multipleGamesInDB_updatesGame() = runBlocking {
        repeat(3) {
            gameDao.createNewGame(generateGameForTest())
        }

        val latest = gameDao.getGame(gameDao.getLastModifiedGameId().first()).first()!!
        latest.moves = arrayOf(1, 2, 3, 4)
        latest.playerOneId = 2L
        latest.playerTwoId = 1L
        latest.lastModified = Date()

        val updatedRows = gameDao.updateGame(latest)
        assertThat(updatedRows, CoreMatchers.equalTo(1))

        val updatedGame = gameDao.getGame(latest.id).first()
        assertThat(updatedGame, CoreMatchers.equalTo(latest))
    }

    @Test
    fun updatingGame_gameDoesNotExistInDatabase_returnsNoRowsChanged() = runBlocking {
        val existingId = gameDao.createNewGame(generateGameForTest())
        val nonexistentId = existingId + 1
        val nonexistentGame = Game(id = nonexistentId, playerOneId = 1L, playerTwoId = 2L)
        val updatedRows = gameDao.updateGame(nonexistentGame)
        assertThat(updatedRows, CoreMatchers.equalTo(0))
    }

    @Test
    fun deletingGame_gameDoesNotExistInDatabase_returnsNoRowsChanged() = runBlocking {
        val existingId = gameDao.createNewGame(generateGameForTest())
        val nonexistentId = existingId + 1

        val deletedRows =
            gameDao.deleteGame(Game(id = nonexistentId, playerOneId = 1L, playerTwoId = 2L))

        assertThat(deletedRows, CoreMatchers.equalTo(0))
        assertThat(gameDao.getAllGames().first().size, CoreMatchers.equalTo(1))
    }

    @Test
    fun deletingGame_multipleGamesCreated_returnsOneRowChanged_leavesOtherGames() = runBlocking {
        val realIds = mutableListOf<Long>()

        repeat(3) {
            realIds.add(gameDao.createNewGame(generateGameForTest()))
        }

        val preDeleteGamesList = gameDao.getAllGames().first()
        val deletedRows = gameDao.deleteGame(preDeleteGamesList[0])
        val postDeleteGamesList = gameDao.getAllGames().first()

        assertThat(deletedRows, CoreMatchers.equalTo(1))
        assertThat(postDeleteGamesList.size, CoreMatchers.equalTo(2))
        assertThat(postDeleteGamesList, CoreMatchers.equalTo(preDeleteGamesList.subList(1, 3)))
    }
}
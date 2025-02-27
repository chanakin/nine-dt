package com.homework.ninedt.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.homework.ninedt.R
import com.homework.ninedt.data.api.NineDTApiService
import com.homework.ninedt.data.database.GameDao
import com.homework.ninedt.data.database.GameDatabase
import com.homework.ninedt.data.model.Game
import com.homework.ninedt.data.utils.BASE_URL
import com.homework.ninedt.data.utils.ioThread
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): NineDTApiService =
        retrofit.create(NineDTApiService::class.java)

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context,
        gameDaoProvider: Provider<GameDao>
    ): GameDatabase {
        return Room.databaseBuilder(context, GameDatabase::class.java, GameDatabase.GAME_DB_NAME)
            .addCallback(
                object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Initialize the database with the first game
                        ioThread {
                            initializeDatabase(context, gameDaoProvider)
                        }
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)

                        ioThread {
                            val gameDao = gameDaoProvider.get()
                            if (gameDao.gameCount() == 0) {
                                // Ensure there is always one game in the database
                                // This will capture the case of the app storage
                                // being cleared
                                initializeDatabase(context, gameDaoProvider)
                            }
                        }
                    }
                }
            ).build()
    }

    @Singleton
    @Provides
    fun provideGameDao(
        database: GameDatabase
    ): GameDao {
        return database.gameDao()
    }

    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO

    private fun initializeDatabase(context: Context, gameDaoProvider: Provider<GameDao>) {
        // TODO I would propose extending this to build actual "Player" objects
        // into the model for a more robust gaming experience (this would enable online support,
        // or even just local multiplayer support)
        val playerId = 1L
        val computerId = 2L

        gameDaoProvider.get()
            .createNewGame(
                Game(
                    playerOneId = playerId,
                    playerTwoId = computerId
                )
            )

        context.getSharedPreferences(
            context.getString(R.string.shared_prefs_file_key),
            Context.MODE_PRIVATE
        ).edit()
            .putLong(context.getString(R.string.player_id), 1L)
            .putLong(context.getString(R.string.computer_AI_player_id), 2L)
            .apply()
    }
}
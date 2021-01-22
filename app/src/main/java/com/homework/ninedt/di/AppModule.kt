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
        val intercepter = HttpLoggingInterceptor()
        intercepter.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder().addInterceptor(intercepter).build()
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
                            val newGameId = gameDaoProvider.get().createNewGame(Game())
                            context.getSharedPreferences(
                                context.getString(R.string.shared_prefs_file_key),
                                Context.MODE_PRIVATE
                            ).edit().putLong(context.getString(R.string.game_id_key), newGameId)
                        }
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)

                        // Ensure there is always one game in the database
                        // This will capture the case of the app storage
                        // being cleared
                        ioThread {
                            val gameDao = gameDaoProvider.get()
                            if (gameDao.gameCount() == 0) {
                                val newGameId = gameDaoProvider.get().createNewGame(Game())
                                context.getSharedPreferences(
                                    context.getString(R.string.shared_prefs_file_key),
                                    Context.MODE_PRIVATE
                                ).edit().putLong(context.getString(R.string.game_id_key), newGameId)
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
}

///**
// * The binding for GameRepository is on its own module so that we can replace it easily in tests.
// */
//@Module
//@InstallIn(ApplicationComponent::class)
//object GameRepositoryModule {
//
//    @Singleton
//    @Provides
//    fun provideGameRepository(
//        @AppModule.RemoteTasksDataSource remoteTasksDataSource: TasksDataSource,
//        @AppModule.LocalTasksDataSource localTasksDataSource: TasksDataSource,
//        ioDispatcher: CoroutineDispatcher
//    ): TasksRepository {
//        return DefaultTasksRepository(
//            remoteTasksDataSource, localTasksDataSource, ioDispatcher
//        )
//    }
//}
package com.homework.ninedt.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

//    @Qualifier
//    @Retention(AnnotationRetention.RUNTIME)
//    annotation class RemoteTasksDataSource
//
//    @Qualifier
//    @Retention(AnnotationRetention.RUNTIME)
//    annotation class LocalTasksDataSource

//    @Singleton
//    @RemoteTasksDataSource
//    @Provides
//    fun provideGameDataSource(): TasksDataSource {
//        return TasksRemoteDataSource
//    }

    @Singleton
    @Provides
    fun provideOkHttpClient() = OkHttpClient.Builder().build()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): NineDTApiService =
        retrofit.create(NineDTApiService::class.java)

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): GameDatabase {
        return Room.databaseBuilder(context, GameDatabase::class.java, GameDatabase.GAME_DB_NAME)
            .addCallback(
                object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Initializes the database with the first game
//                        ioThread {
//                            provideDatabase(context).gameDao().createNewGame(Game())
//                        }
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
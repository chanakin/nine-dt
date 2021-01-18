package com.homework.ninedt.ui.main.viewmodel

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.homework.ninedt.data.api.NetworkService
import com.homework.ninedt.data.database.GameDatabase
import com.homework.ninedt.data.repository.GameRepository

interface ViewModelFactoryProvider {
    fun provideBoardViewModelFactory(context: Context): BoardViewModelFactory
}

val Injector: ViewModelFactoryProvider
    get() = currentInjector

private object DefaultViewModelProvider: ViewModelFactoryProvider {
    private fun getNineDTMovesRepository(context: Context): GameRepository {
        return GameRepository.getInstance(
            gameDao(context),
            gameService()
        )
    }

    private fun gameService() = NetworkService()

    private fun gameDao(context: Context) =
        GameDatabase.getInstance(context.applicationContext).gameDao()

    override fun provideBoardViewModelFactory(context: Context): BoardViewModelFactory {
        val repository = getNineDTMovesRepository(context)
        return BoardViewModelFactory(repository)
    }
}

private object Lock

@Volatile private var currentInjector: ViewModelFactoryProvider =
    DefaultViewModelProvider


@VisibleForTesting
private fun setInjectorForTesting(injector: ViewModelFactoryProvider?) {
    synchronized(Lock) {
        currentInjector = injector ?: DefaultViewModelProvider
    }
}

@VisibleForTesting
private fun resetInjector() =
    setInjectorForTesting(null)
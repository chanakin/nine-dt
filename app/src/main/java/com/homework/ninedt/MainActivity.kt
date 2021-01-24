package com.homework.ninedt

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.homework.ninedt.data.model.GameStatus
import com.homework.ninedt.data.utils.fragmentAdded
import com.homework.ninedt.ui.main.view.BoardFragment
import com.homework.ninedt.ui.main.view.GameOverFragment
import com.homework.ninedt.ui.main.view.StartGameDialogFragment
import com.homework.ninedt.ui.main.viewmodel.GameViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        gameViewModel.status.observe(this, { status ->
            when (status) {
                GameStatus.INITIALIZED -> {
                    showStartGameDialog()
                    supportFragmentManager.findFragmentByTag(GameOverFragment.TAG)?.let {
                        supportFragmentManager.beginTransaction().hide(it).commitNow()
                    }
                }
                GameStatus.COMPLETED -> showGameOver()
                else -> {
                    supportFragmentManager.findFragmentByTag(GameOverFragment.TAG)?.let {
                        supportFragmentManager.beginTransaction().hide(it).commitNow()
                    }
                }
            }
        })

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.board_container, BoardFragment.newInstance(), BoardFragment.TAG)
                .commitNow()
        }
    }

    private fun showStartGameDialog() {
        if (!supportFragmentManager.fragmentAdded((StartGameDialogFragment.TAG))) {
            StartGameDialogFragment().show(
                supportFragmentManager,
                StartGameDialogFragment.TAG
            )
        }
    }

    private fun showGameOver() {
        if (!supportFragmentManager.fragmentAdded(GameOverFragment.TAG)) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.game_result_container,
                    GameOverFragment.newInstance(),
                    GameOverFragment.TAG
                )
                .commitNow()
        } else {
            supportFragmentManager.findFragmentByTag(GameOverFragment.TAG)?.let {
                supportFragmentManager.beginTransaction().show(it).commitNow()
            }
        }
    }
}
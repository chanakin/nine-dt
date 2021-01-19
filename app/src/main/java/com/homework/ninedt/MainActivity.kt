package com.homework.ninedt

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.homework.ninedt.ui.main.view.BoardFragment
import com.homework.ninedt.ui.main.view.StartGameDialogFragment
import com.homework.ninedt.ui.main.viewmodel.BoardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val boardViewModel: BoardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            boardViewModel.game.observe(this, { game ->
                if (game == null) {
                    return@observe
                }

                Log.i("MainActivity", "Game update received $game")
                Toast.makeText(
                    baseContext,
                    "Received update for game. Is it ready: ${game}",
                    Toast.LENGTH_SHORT
                ).show()

                if (!game.readyToPlay()) {
                    StartGameDialogFragment().show(
                        supportFragmentManager,
                        StartGameDialogFragment.TAG
                    )
                    return@observe
                }
            })

            supportFragmentManager.beginTransaction()
                .replace(R.id.container, BoardFragment.newInstance())
                .commitNow()
        }
    }
}
package com.homework.ninedt

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.homework.ninedt.data.model.GameStatus
import com.homework.ninedt.data.utils.fragmentAdded
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
            boardViewModel.status.observe(this, { status ->
                if (status == GameStatus.INITIALIZED
                    && !supportFragmentManager.fragmentAdded(StartGameDialogFragment.TAG)
                ) {
                    StartGameDialogFragment().show(
                        supportFragmentManager,
                        StartGameDialogFragment.TAG
                    )
                } else if (status == GameStatus.COMPLETED) {

                }
            })

            supportFragmentManager.beginTransaction()
                .replace(R.id.container, BoardFragment.newInstance())
                .commitNow()
        }
    }
}
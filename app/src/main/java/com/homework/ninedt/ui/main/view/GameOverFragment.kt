package com.homework.ninedt.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.homework.ninedt.R
import com.homework.ninedt.ui.main.viewmodel.BoardViewModel

class GameOverFragment : Fragment() {

    companion object {
        fun newInstance() = GameOverFragment()
    }

    private val viewModel: BoardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.game_over_fragment, container, false)
    }
}
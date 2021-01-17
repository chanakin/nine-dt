package com.homework.ninedt.ui.main.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.homework.ninedt.R
import com.homework.ninedt.ui.main.viewmodel.BoardViewModel

class BoardFragment : Fragment() {

    companion object {
        fun newInstance() = BoardFragment()
    }

    private val viewModel: BoardViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.main_fragment, container, false)

//        val gridSizeObserver = Observer<Int>{ gridSize ->
//            // update the gridlayout
//        }


        return view
    }
}
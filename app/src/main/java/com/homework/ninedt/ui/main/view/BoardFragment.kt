package com.homework.ninedt.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.homework.ninedt.R
import com.homework.ninedt.databinding.BoardFragmentBinding
import com.homework.ninedt.ui.main.viewmodel.BoardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BoardFragment : Fragment() {

    companion object {
        fun newInstance() = BoardFragment()
    }

    private val viewModel: BoardViewModel by activityViewModels()

    private var _binding: BoardFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BoardFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel.game.observe(viewLifecycleOwner) { game ->
            Snackbar.make(binding.root, "Game updated with moves $game.moves", Snackbar.LENGTH_LONG)
                .show()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
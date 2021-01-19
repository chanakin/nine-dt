package com.homework.ninedt.ui.main.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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

        viewModel.board.observe(viewLifecycleOwner) { board ->
            Log.i("BoardFragment", "Redrawing board...")
            redrawBoard(board)
        }

        return view
    }

    private fun redrawBoard(board: List<List<Int>>) {
        view?.let { boardView ->
            board.forEachIndexed() { rowIndex, row ->
                row.forEachIndexed { columnIndex, _ ->

                    // find the right view
                    val correspondingTokenViewId = "row_${rowIndex + 1}_column_${columnIndex + 1}"
                    val correspondingTokenViewResId =
                        resources.getIdentifier(
                            correspondingTokenViewId,
                            "id",
                            requireContext().packageName
                        )

                    val tokenView = boardView.findViewById<ImageView>(correspondingTokenViewResId)
                    // Player 1 is red, Player 2 is yellow
                    val whichPlayerToken = when (board[rowIndex][columnIndex]) {
                        1 -> R.drawable.red_token
                        2 -> R.drawable.yellow_token
                        else -> R.drawable.empty_token
                    }

                    tokenView.setImageResource(whichPlayerToken)
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
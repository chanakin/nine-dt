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
        const val TAG = "BoardFragment"
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
            Log.i("BoardFragment", "Redrawing board... $board")
            redrawBoard(board)
        }

        viewModel.isMyTurn.observe(viewLifecycleOwner) { myTurn ->
            Log.i(TAG, "Turn changed: my turn? $myTurn")
            enablePlay(myTurn)
            binding.turnInstructions.text =
                if (myTurn) getString(R.string.your_move_instructions) else getString(R.string.other_player_move_instructions)
        }

        setOnClickListenersForDroppingToken()
        return view
    }

    private fun setOnClickListenersForDroppingToken() {
        binding.column0.setOnClickListener {
            viewModel.dropToken(0)
        }

        binding.column1.setOnClickListener {
            viewModel.dropToken(1)
        }

        binding.column2.setOnClickListener {
            viewModel.dropToken(2)
        }

        binding.column3.setOnClickListener {
            viewModel.dropToken(3)
        }
    }

    private fun enablePlay(enable: Boolean) {
        binding.column0.isEnabled = enable
        binding.column1.isEnabled = enable
        binding.column2.isEnabled = enable
        binding.column3.isEnabled = enable
    }

    private fun redrawBoard(board: Array<Array<Int>>) {
        view?.let { boardView ->
            board.forEachIndexed { columnIndex, column ->
                column.forEachIndexed { rowIndex, _ ->

                    // find the right view
                    val correspondingTokenViewId = "row_${rowIndex}_column_${columnIndex}"
                    val correspondingTokenViewResId =
                        resources.getIdentifier(
                            correspondingTokenViewId,
                            "id",
                            requireContext().packageName
                        )

                    val tokenView = boardView.findViewById<ImageView>(correspondingTokenViewResId)
                    // Player 1 is red, Player 2 is yellow
                    val whichPlayerToken = when (board[columnIndex][rowIndex]) {
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
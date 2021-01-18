package com.homework.ninedt.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.homework.ninedt.R
import com.homework.ninedt.databinding.BoardFragmentBinding
import com.homework.ninedt.ui.main.viewmodel.BoardViewModel

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

        viewModel.boardGridSize.observe(viewLifecycleOwner) { gridSize ->
            binding.boardLayout.removeAllViews()

            repeat(gridSize * gridSize) {
                addTokenView(tokenColor = ' ')
            }
        }

        return view
    }

    private fun addRowToGrid(gridSize: Int) {
        val addedViews: MutableList<View> = mutableListOf()

        repeat(gridSize) {
            addedViews.add(addTokenView(' '))
        }

        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.boardLayout)

        addedViews.forEachIndexed { index, view ->
            if (index == 0) {
                constraintSet.connect(
                    view.id,
                    ConstraintSet.LEFT,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.LEFT
                )
            } else {
                constraintSet.connect(
                    view.id,
                    ConstraintSet.LEFT,
                    addedViews[index - 1].id,
                    ConstraintSet.RIGHT
                )

                if (index == addedViews.size - 1) {
                    constraintSet.connect(
                        view.id,
                        ConstraintSet.RIGHT,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.RIGHT
                    )
                }
            }
        }

        val viewIds = addedViews.map { view -> view.id }.toIntArray()

        constraintSet.createHorizontalChain(
            ConstraintSet.PARENT_ID,
            ConstraintSet.LEFT,
            ConstraintSet.PARENT_ID,
            ConstraintSet.RIGHT,
            viewIds,
            null,
            ConstraintSet.CHAIN_SPREAD
        )
        constraintSet.applyTo(binding.boardLayout)
    }

    private fun addTokenView(tokenColor: Char): View {
        val token = View(context)
        token.id = View.generateViewId()

        val drawable = when (tokenColor) {
            'R' -> R.drawable.red_token
            'B' -> R.drawable.yellow_token
            else -> R.drawable.empty_token
        }

        token.setBackgroundResource(drawable)
        binding.boardLayout.addView(token)
        return token
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
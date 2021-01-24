package com.homework.ninedt.ui.main.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.homework.ninedt.R
import com.homework.ninedt.data.model.GameStatus
import com.homework.ninedt.databinding.GameOverFragmentBinding
import com.homework.ninedt.ui.main.viewmodel.GameViewModel
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

class GameOverFragment : Fragment() {

    companion object {
        fun newInstance() = GameOverFragment()
        const val TAG = "GameOverFragment"
    }

    private val viewModel: GameViewModel by activityViewModels()

    private var _binding: GameOverFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GameOverFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        view.isClickable = true

        val myPlayerId = requireContext().getSharedPreferences(
            getString(R.string.shared_prefs_file_key),
            Context.MODE_PRIVATE
        ).getLong(getString(R.string.player_id), 1L)

        viewModel.game.observe(viewLifecycleOwner) { game ->
            if (game.winningPlayerId == myPlayerId) {
                binding.resultsText.text = getString(R.string.win_message)
                binding.confettiView.build()
                    .addColors(
                        view.context.getColor(R.color.redToken),
                        view.context.getColor(R.color.yellowToken),
                        view.context.getColor(R.color.redTokenEdge),
                        view.context.getColor(R.color.yellowTokenEdge),
                        view.context.getColor(R.color.colorAccent)
                    )
                    .setDirection(0.0, 359.0)
                    .setSpeed(1f, 5f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(2000L)
                    .addShapes(Shape.Square, Shape.Circle)
                    .addSizes(Size(12), Size(16, 6f))
                    .setPosition(-50f, binding.confettiView.width + 50f, -50f, -50f)
                    .streamFor(300, 5000L)
            } else if (game.winningPlayerId == null) {
                binding.resultsText.text = getString(R.string.everybody_loses)
            } else {
                binding.resultsText.text = getString(R.string.lose_message)
            }
        }

        viewModel.status.observe(viewLifecycleOwner) {
            status ->
            if (status != GameStatus.COMPLETED) {
                // We've moved on. Stop throwing confetti!
                binding.confettiView.reset()
            }
        }

        binding.startNewGameButton.setOnClickListener {
            viewModel.createNewGame()
        }

        return view
    }
}
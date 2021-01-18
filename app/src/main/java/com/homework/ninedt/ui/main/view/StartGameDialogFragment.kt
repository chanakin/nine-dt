package com.homework.ninedt.ui.main.view

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.homework.ninedt.R
import com.homework.ninedt.ui.main.viewmodel.BoardViewModel
import kotlinx.coroutines.launch
import kotlin.random.Random

class StartGameDialogFragment : DialogFragment() {
    private val viewModel: BoardViewModel by activityViewModels()

    init {
        lifecycleScope.launch {
            whenStarted {

                viewModel.startingPlayer.observe(viewLifecycleOwner) { newStartingPlayer ->
                    if (newStartingPlayer == 0) {
                        show(parentFragmentManager, TAG)
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "StartGameDialogFragment"
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewModel.currentPlayer
        return MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.start_game_message)
            .setTitle(R.string.start_game_dialog_title)
            .setSingleChoiceItems(
                R.array.start_game_starting_player_options,
                0
            ) { _, optionSelected ->
                viewModel.setStartingPlayer(
                    when (optionSelected) {
                        0 -> 0
                        1 -> 1
                        2 -> Random.nextInt(0, 1)
                        else -> 0
                    }
                )
            }
            .setPositiveButton(R.string.start_game_positive_button) { _, _ ->
                viewModel.startGame()
                dismiss()
            }
            .create()
    }
}
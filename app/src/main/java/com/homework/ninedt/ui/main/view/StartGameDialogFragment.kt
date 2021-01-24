package com.homework.ninedt.ui.main.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.homework.ninedt.R
import com.homework.ninedt.ui.main.viewmodel.GameViewModel

class StartGameDialogFragment : DialogFragment() {
    private val viewModel: GameViewModel by activityViewModels()

    companion object {
        const val TAG = "StartGameDialogFragment"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val myPlayerId = context?.let {
            it.getSharedPreferences(
                getString(R.string.shared_prefs_file_key),
                Context.MODE_PRIVATE
            ).getLong(it.getString(R.string.player_id), 1L)
        } ?: 1L

        val computerPlayerId = context?.let {
            it.getSharedPreferences(
                getString(R.string.shared_prefs_file_key),
                Context.MODE_PRIVATE
            ).getLong(it.getString(R.string.computer_AI_player_id), 2L)
        } ?: 2L

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.start_game_message)
            .setSingleChoiceItems(
                R.array.start_game_starting_player_options,
                0
            ) { _, optionSelected ->
                viewModel.setStartingPlayer(
                    when (optionSelected) {
                        0 -> myPlayerId
                        1 -> computerPlayerId
                        else -> myPlayerId
                    }
                )
            }
            .setPositiveButton(R.string.start_game_positive_button) { _, _ ->
                viewModel.startGame()
                dismiss()
            }
            .create()

        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        isCancelable = false
        return dialog
    }
}
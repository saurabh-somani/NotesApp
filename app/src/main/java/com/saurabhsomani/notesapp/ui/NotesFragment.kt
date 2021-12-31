package com.saurabhsomani.notesapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.saurabhsomani.notesapp.adapter.NotesAdapter
import com.saurabhsomani.notesapp.databinding.NotesFragmentBinding
import com.saurabhsomani.notesapp.util.getResStrByName
import com.saurabhsomani.notesapp.viewmodels.NotesEvent
import com.saurabhsomani.notesapp.viewmodels.NotesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotesFragment : Fragment() {

    private lateinit var binding: NotesFragmentBinding
    private val viewModel by viewModels<NotesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NotesFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.notesAdapter = NotesAdapter()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated: ")
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    onNotesAdapterUpdates()
                }
                launch {
                    onNavEventUpdates()
                }
                launch {
                    onSnackBarEventUpdates()
                }
            }
        }
    }

    private suspend fun onSnackBarEventUpdates() {
        viewModel.uiState.collect { notesUiState ->
            Log.d(TAG, "onSnackBarEventUpdates: $notesUiState")
            notesUiState.snackBarEvents.firstOrNull()?.let {
                showSnackBar(it)
                viewModel.onSnackBarShown(it.id)
            }
        }
    }

    private fun showSnackBar(snackBarEvent: NotesEvent.SnackBarEvent) {
        Log.d(TAG, "showSnackBar: ")
        Snackbar.make(
            binding.root,
            context.getResStrByName(snackBarEvent.messageResStr),
            Snackbar.LENGTH_LONG
        ).setAction(context.getResStrByName(snackBarEvent.actionTextResStr)) {
            snackBarEvent.onActionClick.invoke()
        }.show()
    }

    private suspend fun onNotesAdapterUpdates() {
        viewModel.notesUiItems.collect {
            binding.notesAdapter?.submitList(it)
        }
    }

    private suspend fun onNavEventUpdates() {
        viewModel.uiState.collect { notesUiState ->
            Log.d(TAG, "onNavEventUpdates: $notesUiState")
            notesUiState.navEvents.firstOrNull()?.let {
                navigateToDetailFragment(it.noteId)
                viewModel.onNavComplete()
            }
        }
    }

    private fun navigateToDetailFragment(noteId: Long) {
        val action = NotesFragmentDirections.actionNotesFragmentToNoteDetailFragment(noteId)
        view?.findNavController()?.navigate(action)
    }

    companion object {
        private const val TAG = "NotesFragment"
    }
}
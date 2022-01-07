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
import com.saurabhsomani.notesapp.viewmodels.NotesViewModel
import com.saurabhsomani.notesapp.viewmodels.NotesViewModel.NotesEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
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
        receiveUiStateUpdates()
    }

    private fun receiveUiStateUpdates() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    setUsername()
                }
                launch {
                    onNotesAdapterUpdates()
                }
                launch {
                    onNavEventUpdates()
                }
                launch {
                    onSnackBarEventUpdates()
                }
                launch {
                    onNotesRefreshUpdates()
                }
            }
        }
    }

    private suspend fun onNotesRefreshUpdates() {
        viewModel.uiState
            .map { it.notesRefreshing }
            .collect { refreshing ->
                binding.swipeRefreshNotes.isRefreshing = refreshing
            }
    }

    private suspend fun setUsername() {
        viewModel.uiState
            .map { it.username }
            .collect {
                binding.notesUsername.text = it
            }
    }

    private suspend fun onSnackBarEventUpdates() {
        viewModel.uiState
            .map { it.snackbarEvents }
            .distinctUntilChanged()
            .collect { snackbarEvents ->
                Log.d(TAG, "onSnackBarEventUpdates: $snackbarEvents")
                snackbarEvents.firstOrNull()?.let {
                    showSnackBar(it)
                    viewModel.onEvent(NotesEvent.OnSnackbarShown(it.id))
                }
            }
    }

    private fun showSnackBar(snackBarEvent: NotesViewModel.NotesUiEvent.SnackbarEvent) {
        Log.d(TAG, "showSnackBar: $snackBarEvent")
        Snackbar.make(
            binding.root,
            context.getResStrByName(snackBarEvent.messageResStr),
            Snackbar.LENGTH_LONG
        ).setAction(context.getResStrByName(snackBarEvent.actionTextResStr)) {
            snackBarEvent.onActionClick.invoke()
        }.show()
    }

    private suspend fun onNotesAdapterUpdates() {
        viewModel.uiState
            .map { it.notesUiItems }
            .distinctUntilChanged()
            .collect { noteItemUiStateList ->
                Log.d(TAG, "onNotesAdapterUpdates: $noteItemUiStateList")
                binding.notesAdapter?.submitList(noteItemUiStateList)
            }
    }

    private suspend fun onNavEventUpdates() {
        viewModel.uiState
            .map { it.navEvents }
            .distinctUntilChanged()
            .collect { navEvents ->
                Log.d(TAG, "onNavEventUpdates: $navEvents")
                navEvents.firstOrNull()?.let {
                    navigateToDetailFragment(it.noteId)
                    viewModel.onEvent(NotesEvent.OnNavComplete)
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
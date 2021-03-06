package com.saurabhsomani.notesapp.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.saurabhsomani.notesapp.R
import com.saurabhsomani.notesapp.databinding.NoteDetailFragmentBinding
import com.saurabhsomani.notesapp.viewmodels.NoteDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoteDetailFragment : Fragment() {

    private lateinit var binding: NoteDetailFragmentBinding
    private val viewModel by viewModels<NoteDetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NoteDetailFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.noteTitle.addTextChangedListener {
            Log.d(TAG, "noteTitle doAfterTextChanged: ")
//            viewModel.saveNoteTitle(it.toString())
            viewModel.updateTitle(it.toString())
        }

        binding.noteDescription.addTextChangedListener {
            Log.d(TAG, "noteDescription doAfterTextChanged: ")
//            viewModel.saveNoteDescription(it.toString())
            viewModel.updateDescription(it.toString())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { noteDetailUiState ->
                    binding.root.isVisible = !noteDetailUiState.isLoading
                    binding.noteTitle.setText(noteDetailUiState.title)
                    binding.noteDescription.setText(noteDetailUiState.description)
                    binding.noteTimestamp.text = getString(
                        R.string.note_detail_timestamp_text,
                        noteDetailUiState.timestamp
                    )
                    activity?.invalidateOptionsMenu()
                }
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val signOutItem = menu.findItem(R.id.action_sign_out)
        signOutItem.isVisible = false

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .map { it.saveBtnVisible }
                    .collect { enabled ->
                        val saveItem = menu.findItem(R.id.action_save_note)
                        saveItem.isVisible = enabled
                    }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save_note -> {
                viewModel.saveNote()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val TAG = "NoteDetailFragment"
    }
}
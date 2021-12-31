package com.saurabhsomani.notesapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.saurabhsomani.notesapp.databinding.NotesListItemBinding
import com.saurabhsomani.notesapp.viewmodels.NoteItemUiState

class NotesAdapter : ListAdapter<NoteItemUiState, NotesAdapter.NotesViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder.inflate(parent)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun onItemSwiped(position: Int) {
        val notesUiState = getItem(position)
        notesUiState?.onSwipe?.invoke()
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<NoteItemUiState>() {
            override fun areItemsTheSame(
                oldItem: NoteItemUiState,
                newItem: NoteItemUiState
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: NoteItemUiState,
                newItem: NoteItemUiState
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    class NotesViewHolder private constructor(
        private val binding: NotesListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(noteItemUiState: NoteItemUiState) {
            binding.noteItemUiState = noteItemUiState
        }

        companion object {
            fun inflate(parent: ViewGroup) = NotesViewHolder(
                NotesListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }
}
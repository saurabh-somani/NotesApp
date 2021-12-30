package com.example.notesapp.adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("onSwiped")
fun setSwipeFunctionality(recyclerView: RecyclerView, adapter: NotesAdapter) {
    ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
        ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            adapter.onItemSwiped(viewHolder.adapterPosition)
        }
    }).attachToRecyclerView(recyclerView)
}
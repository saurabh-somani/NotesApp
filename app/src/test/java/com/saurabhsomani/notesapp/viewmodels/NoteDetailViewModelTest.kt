package com.saurabhsomani.notesapp.viewmodels

import androidx.lifecycle.SavedStateHandle
import com.saurabhsomani.notesapp.MainCoroutineRule
import com.saurabhsomani.notesapp.repository.NotesRepo
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class NoteDetailViewModelTest {

    private lateinit var viewModel: NoteDetailViewModel
    @MockK
    private lateinit var repo: NotesRepo
    @MockK
    private lateinit var savedStateHandle: SavedStateHandle

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        every { savedStateHandle.get<Long>("noteId") } returns 0L
        viewModel = NoteDetailViewModel(repo, savedStateHandle)
    }

    @Test
    fun updateTitle() = runTest {
        val title = "Sample title"
        viewModel.updateTitle(title)
        Assert.assertEquals(title, viewModel.uiState.first().title)
    }

    @Test
    fun updateDescription() = runTest {
        val desc = "Sample description"
        viewModel.updateDescription(desc)
        Assert.assertEquals(desc, viewModel.uiState.first().description)
    }
}
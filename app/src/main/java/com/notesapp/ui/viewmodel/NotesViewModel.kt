package com.notesapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notesapp.data.Note
import com.notesapp.data.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotesViewModel @Inject constructor(
    private val repository: NotesRepository
) : ViewModel() {
    
    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes
    
    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery
    
    private val _isSearching = MutableLiveData<Boolean>()
    val isSearching: LiveData<Boolean> = _isSearching
    
    init {
        loadNotes()
    }
    
    fun loadNotes() {
        viewModelScope.launch {
            repository.getAllNotes().collect { notesList ->
                _notes.value = notesList
            }
        }
    }
    
    fun searchNotes(query: String) {
        _searchQuery.value = query
        _isSearching.value = query.isNotEmpty()
        
        viewModelScope.launch {
            if (query.isEmpty()) {
                repository.getAllNotes().collect { notesList ->
                    _notes.value = notesList
                }
            } else {
                repository.searchNotes(query).collect { notesList ->
                    _notes.value = notesList
                }
            }
        }
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
        _isSearching.value = false
        loadNotes()
    }
    
    fun insertNote(title: String, content: String) {
        viewModelScope.launch {
            val note = Note(
                title = title,
                content = content
            )
            repository.insertNote(note)
        }
    }
    
    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note)
        }
    }
    
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }
    
    fun deleteNoteById(id: Long) {
        viewModelScope.launch {
            repository.deleteNoteById(id)
        }
    }
}

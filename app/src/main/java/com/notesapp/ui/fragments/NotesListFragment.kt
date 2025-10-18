package com.notesapp.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.notesapp.R
import com.notesapp.data.Note
import com.notesapp.databinding.FragmentNotesListBinding
import com.notesapp.ui.adapter.NotesAdapter
import com.notesapp.ui.viewmodel.NotesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotesListFragment : Fragment() {
    
    private var _binding: FragmentNotesListBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: NotesViewModel
    private lateinit var notesAdapter: NotesAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupRecyclerView()
        setupFab()
        setupSearchView()
        observeViewModel()
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[NotesViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        notesAdapter = NotesAdapter(
            onNoteClick = { note ->
                navigateToNoteDetail(note)
            },
            onNoteDelete = { note ->
                viewModel.deleteNote(note)
            }
        )
        
        binding.recyclerViewNotes.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = notesAdapter
        }
    }
    
    private fun setupFab() {
        binding.fabAddNote.setOnClickListener {
            findNavController().navigate(R.id.action_notesListFragment_to_addNoteFragment)
        }
    }
    
    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchNotes(newText ?: "")
                return true
            }
        })
    }
    
    private fun observeViewModel() {
        viewModel.notes.observe(viewLifecycleOwner) { notes ->
            notesAdapter.submitList(notes)
            binding.textEmptyState.visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE
        }
        
        viewModel.isSearching.observe(viewLifecycleOwner) { isSearching ->
            binding.textEmptyState.text = if (isSearching) {
                "No notes found"
            } else {
                "No notes yet. Tap + to add your first note!"
            }
        }
    }
    
    private fun navigateToNoteDetail(note: Note) {
        val action = NotesListFragmentDirections.actionNotesListFragmentToNoteDetailFragment(note)
        findNavController().navigate(action)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

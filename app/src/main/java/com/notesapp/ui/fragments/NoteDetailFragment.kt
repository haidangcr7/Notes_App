package com.notesapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.notesapp.R
import com.notesapp.data.Note
import com.notesapp.databinding.FragmentNoteDetailBinding
import com.notesapp.ui.viewmodel.NotesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteDetailFragment : Fragment() {
    
    private var _binding: FragmentNoteDetailBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: NotesViewModel
    private val args: NoteDetailFragmentArgs by navArgs()
    private var currentNote: Note? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        loadNote()
        setupToolbar()
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[NotesViewModel::class.java]
    }
    
    private fun loadNote() {
        currentNote = args.note
        displayNote(currentNote)
    }
    
    private fun displayNote(note: Note?) {
        note?.let {
            binding.textTitle.text = it.title
            binding.textContent.text = it.content
            binding.textDate.text = "Created: ${formatDate(it.createdAt)}"
            binding.textUpdated.text = "Updated: ${formatDate(it.updatedAt)}"
        }
    }
    
    private fun formatDate(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        val format = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())
        return format.format(date)
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_note_detail, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                navigateToEditNote()
                true
            }
            R.id.action_delete -> {
                deleteNote()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun navigateToEditNote() {
        currentNote?.let { note ->
            val action = NoteDetailFragmentDirections.actionNoteDetailFragmentToEditNoteFragment(note)
            findNavController().navigate(action)
        }
    }
    
    private fun deleteNote() {
        currentNote?.let { note ->
            viewModel.deleteNote(note)
            Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

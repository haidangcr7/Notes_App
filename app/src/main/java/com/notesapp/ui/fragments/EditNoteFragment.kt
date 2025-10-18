package com.notesapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.notesapp.databinding.FragmentEditNoteBinding
import com.notesapp.ui.viewmodel.NotesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditNoteFragment : Fragment() {
    
    private var _binding: FragmentEditNoteBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var viewModel: NotesViewModel
    private val args: EditNoteFragmentArgs by navArgs()
    private var currentNote: com.notesapp.data.Note? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        loadNote()
        setupToolbar()
        setupSaveButton()
    }
    
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[NotesViewModel::class.java]
    }
    
    private fun loadNote() {
        currentNote = args.note
        currentNote?.let { note ->
            binding.editTextTitle.setText(note.title)
            binding.editTextContent.setText(note.content)
        }
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun setupSaveButton() {
        binding.buttonSave.setOnClickListener {
            updateNote()
        }
    }
    
    private fun updateNote() {
        val title = binding.editTextTitle.text.toString().trim()
        val content = binding.editTextContent.text.toString().trim()
        
        if (title.isEmpty()) {
            binding.editTextTitle.error = "Title is required"
            return
        }
        
        if (content.isEmpty()) {
            binding.editTextContent.error = "Content is required"
            return
        }
        
        currentNote?.let { note ->
            val updatedNote = note.copy(
                title = title,
                content = content,
                updatedAt = System.currentTimeMillis()
            )
            viewModel.updateNote(updatedNote)
            Toast.makeText(context, "Note updated successfully", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

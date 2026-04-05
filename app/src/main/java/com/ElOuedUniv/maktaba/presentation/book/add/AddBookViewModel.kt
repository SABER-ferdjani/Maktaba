package com.ElOuedUniv.maktaba.presentation.book.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ElOuedUniv.maktaba.data.model.Book
import com.ElOuedUniv.maktaba.domain.usecase.AddBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBookViewModel @Inject constructor(
    private val addBookUseCase: AddBookUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddBookUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: AddBookUiAction) {
        when (action) {
            is AddBookUiAction.OnTitleChange -> {
                _uiState.update { 
                    val newState = it.copy(title = action.title)
                    newState.copy(
                        titleError = if (action.title.isBlank()) "Title cannot be empty" else null,
                        isFormValid = validateInputs(newState)
                    )
                }
            }
            is AddBookUiAction.OnIsbnChange -> {
                _uiState.update { 
                    val newState = it.copy(isbn = action.isbn)
                    newState.copy(
                        isbnError = if (action.isbn.length != 13 || !action.isbn.all { char -> char.isDigit() }) 
                            "ISBN must be exactly 13 digits" else null,
                        isFormValid = validateInputs(newState)
                    )
                }
            }
            is AddBookUiAction.OnPagesChange -> {
                _uiState.update { 
                    val newState = it.copy(nbPages = action.pages)
                    val pagesInt = action.pages.toIntOrNull()
                    newState.copy(
                        nbPagesError = if (pagesInt == null || pagesInt <= 0) "Pages must be a positive number" else null,
                        isFormValid = validateInputs(newState)
                    )
                }
            }
            is AddBookUiAction.OnImageSelected -> {
                _uiState.update { it.copy(imageUrl = action.uri) }
            }
            AddBookUiAction.OnAddClick -> {
                if (_uiState.value.isFormValid) {
                    addBook()
                }
            }
        }
    }

    private fun validateInputs(state: AddBookUiState): Boolean {
        val titleValid = state.title.isNotBlank()
        val isbnValid = state.isbn.length == 13 && state.isbn.all { it.isDigit() }
        val pagesValid = (state.nbPages.toIntOrNull() ?: 0) > 0
        return titleValid && isbnValid && pagesValid
    }

    private fun addBook() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val currentState = _uiState.value
                val book = Book(
                    isbn = currentState.isbn,
                    title = currentState.title,
                    nbPages = currentState.nbPages.toIntOrNull() ?: 0,
                    imageUrl = currentState.imageUrl
                )
                addBookUseCase(book)
                _uiState.update { it.copy(isSuccess = true, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message, isLoading = false) }
            }
        }
    }
}

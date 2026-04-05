package com.ElOuedUniv.maktaba.presentation.book.add

data class AddBookUiState(
    val title: String = "",
    val isbn: String = "",
    val nbPages: String = "",
    val imageUrl: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,

    // Validation States
    val titleError: String? = null,
    val isbnError: String? = null,
    val nbPagesError: String? = null,
    val isFormValid: Boolean = false
)

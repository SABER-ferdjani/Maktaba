package com.ElOuedUniv.maktaba.data.repository

import com.ElOuedUniv.maktaba.data.model.Book
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepositoryImpl @Inject constructor() : BookRepository {

    private val _booksList = mutableListOf(
        Book(isbn = "978-0132350884", title = "Clean Code", nbPages = 464),
        Book(isbn = "978-0201616224", title = "The Pragmatic Programmer", nbPages = 352),
        Book(isbn = "978-0596007126", title = "Head First Design Patterns", nbPages = 694),
        Book(isbn = "978-0134494166", title = "Clean Architecture", nbPages = 432),
        Book(isbn = "978-0132350884", title = "Refactoring", nbPages = 448)
    )

    private val booksFlow = MutableSharedFlow<List<Book>>(replay = 1).apply {
        tryEmit(_booksList.toList())
    }
    
    override fun getAllBooks(): Flow<List<Book>> = flow {
        delay(1000) // Simulate network delay
        emitAll(booksFlow)
    }

    override fun getBookByIsbn(isbn: String): Book? {
        return _booksList.find { it.isbn == isbn }
    }

    override fun addBook(book: Book) {
        _booksList.add(book)
        booksFlow.tryEmit(_booksList.toList())
    }
}

package pt.unl.fct.iadi.bookstore.service

class BookNotFoundException(val isbn: String, val language: String = "en") :
    RuntimeException("Book with ISBN '$isbn' not found")

class ReviewNotFoundException(val reviewId: Long) :
    RuntimeException("Review with id '$reviewId' not found")

class BookAlreadyExistsException(val isbn: String) :
    RuntimeException("Book with ISBN '$isbn' already exists")

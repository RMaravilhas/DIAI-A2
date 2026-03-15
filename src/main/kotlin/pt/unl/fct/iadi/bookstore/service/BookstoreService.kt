package pt.unl.fct.iadi.bookstore.service

import org.springframework.stereotype.Service
import pt.unl.fct.iadi.bookstore.domain.Book
import pt.unl.fct.iadi.bookstore.domain.Review
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@Service
class BookstoreService {

    private val books = ConcurrentHashMap<String, Book>()
    private val reviews = ConcurrentHashMap<Long, Review>()
    private val reviewIdCounter = AtomicLong(1)

    // ─── US1: List all books ─────────────────────────────────────────────────

    fun listBooks(): List<Book> = books.values.toList()

    // ─── US2: Create a book ──────────────────────────────────────────────────

    fun createBook(isbn: String, title: String, author: String, price: Double, image: String): Book {
        if (books.containsKey(isbn)) throw BookAlreadyExistsException(isbn)
        val book = Book(isbn, title, author, price, image)
        books[isbn] = book
        return book
    }

    // ─── US3: Get a single book ──────────────────────────────────────────────

    fun getBook(isbn: String): Book =
        books[isbn] ?: throw BookNotFoundException(isbn)

    // ─── US4: Replace (upsert) a book ────────────────────────────────────────

    /**
     * Returns Pair(book, created) where created=true means it was inserted (201),
     * false means it was replaced (200).
     */
    fun upsertBook(isbn: String, title: String, author: String, price: Double, image: String): Pair<Book, Boolean> {
        val wasAbsent = !books.containsKey(isbn)
        val book = Book(isbn, title, author, price, image)
        books[isbn] = book
        return Pair(book, wasAbsent)
    }

    // ─── US5: Partially update a book ────────────────────────────────────────

    fun patchBook(
        isbn: String,
        title: String?,
        author: String?,
        price: Double?,
        image: String?
    ): Book {
        val book = books[isbn] ?: throw BookNotFoundException(isbn)
        title?.let { book.title = it }
        author?.let { book.author = it }
        price?.let { book.price = it }
        image?.let { book.image = it }
        return book
    }

    // ─── US6: Delete a book ──────────────────────────────────────────────────

    fun deleteBook(isbn: String) {
        books.remove(isbn) ?: throw BookNotFoundException(isbn)
        // remove all reviews belonging to this book
        reviews.entries.removeIf { it.value.bookIsbn == isbn }
    }

    // ─── US7: List reviews for a book ────────────────────────────────────────

    fun listReviews(isbn: String): List<Review> {
        if (!books.containsKey(isbn)) throw BookNotFoundException(isbn)
        return reviews.values.filter { it.bookIsbn == isbn }
    }

    // ─── US8: Create a review ────────────────────────────────────────────────

    fun createReview(isbn: String, rating: Int, comment: String?): Review {
        if (!books.containsKey(isbn)) throw BookNotFoundException(isbn)
        val reviewId = reviewIdCounter.getAndIncrement()
        val review = Review(reviewId, isbn, rating, comment)
        reviews[reviewId] = review
        return review
    }

    // ─── US9: Replace a review ───────────────────────────────────────────────

    fun replaceReview(isbn: String, reviewId: Long, rating: Int, comment: String?): Review {
        if (!books.containsKey(isbn)) throw BookNotFoundException(isbn)
        val review = reviews[reviewId]?.takeIf { it.bookIsbn == isbn }
            ?: throw ReviewNotFoundException(reviewId)
        review.rating = rating
        review.comment = comment
        return review
    }

    // ─── US10: Partially update a review ─────────────────────────────────────

    fun patchReview(isbn: String, reviewId: Long, rating: Int?, comment: String?): Review {
        if (!books.containsKey(isbn)) throw BookNotFoundException(isbn)
        val review = reviews[reviewId]?.takeIf { it.bookIsbn == isbn }
            ?: throw ReviewNotFoundException(reviewId)
        rating?.let { review.rating = it }
        // comment can be set to null (clear it) — we only skip if key was not provided at all
        // but here nullable means "not provided", so we only set if non-null
        comment?.let { review.comment = it }
        return review
    }

    // ─── US11: Delete a review ───────────────────────────────────────────────

    fun deleteReview(isbn: String, reviewId: Long) {
        if (!books.containsKey(isbn)) throw BookNotFoundException(isbn)
        val review = reviews[reviewId]?.takeIf { it.bookIsbn == isbn }
            ?: throw ReviewNotFoundException(reviewId)
        reviews.remove(review.id)
    }
}

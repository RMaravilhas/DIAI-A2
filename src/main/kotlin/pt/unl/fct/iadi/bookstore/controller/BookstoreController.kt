package pt.unl.fct.iadi.bookstore.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import pt.unl.fct.iadi.bookstore.controller.dto.*
import pt.unl.fct.iadi.bookstore.domain.Book
import pt.unl.fct.iadi.bookstore.domain.Review
import pt.unl.fct.iadi.bookstore.service.BookNotFoundException
import pt.unl.fct.iadi.bookstore.service.BookstoreService

@RestController
class BookstoreController(private val service: BookstoreService) : BookstoreAPI {

    // ─── Mappers ──────────────────────────────────────────────────────────────

    private fun Book.toResponse() = BookResponse(isbn, title, author, price, image)
    private fun Review.toResponse() = ReviewResponse(id, rating, comment)

    // ─── Books ────────────────────────────────────────────────────────────────

    override fun listBooks(): ResponseEntity<List<BookResponse>> =
        ResponseEntity.ok(service.listBooks().map { it.toResponse() })

    override fun createBook(body: BookCreateRequest): ResponseEntity<BookResponse> {
        val book = service.createBook(body.isbn, body.title, body.author, body.price, body.image)
        val location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{isbn}")
            .buildAndExpand(book.isbn)
            .toUri()
        return ResponseEntity.created(location).body(book.toResponse())
    }

    override fun getBook(isbn: String, language: String): ResponseEntity<BookResponse> {
        val lang = if (language.trim().lowercase().startsWith("pt")) "pt" else "en"
        return try {
            val book = service.getBook(isbn)
            ResponseEntity.ok()
                .header("Content-Language", lang)
                .body(book.toResponse())
        } catch (e: BookNotFoundException) {
            throw BookNotFoundException(isbn, lang)
        }
    }

    override fun replaceBook(isbn: String, body: BookCreateRequest): ResponseEntity<BookResponse> {
        val (book, created) = service.upsertBook(isbn, body.title, body.author, body.price, body.image)
        return if (created) {
            val location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri()
            ResponseEntity.created(location).body(book.toResponse())
        } else {
            ResponseEntity.ok(book.toResponse())
        }
    }

    override fun patchBook(isbn: String, body: BookPatchRequest): ResponseEntity<BookResponse> {
        val book = service.patchBook(isbn, body.title, body.author, body.price, body.image)
        return ResponseEntity.ok(book.toResponse())
    }

    override fun deleteBook(isbn: String): ResponseEntity<Void> {
        service.deleteBook(isbn)
        return ResponseEntity.noContent().build()
    }

    // ─── Reviews ──────────────────────────────────────────────────────────────

    override fun listReviews(isbn: String): ResponseEntity<List<ReviewResponse>> =
        ResponseEntity.ok(service.listReviews(isbn).map { it.toResponse() })

    override fun createReview(isbn: String, body: ReviewCreateRequest): ResponseEntity<ReviewResponse> {
        val review = service.createReview(isbn, body.rating, body.comment)
        val location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{reviewId}")
            .buildAndExpand(review.id)
            .toUri()
        return ResponseEntity.created(location).body(review.toResponse())
    }

    override fun replaceReview(isbn: String, reviewId: Long, body: ReviewCreateRequest): ResponseEntity<ReviewResponse> {
        val review = service.replaceReview(isbn, reviewId, body.rating, body.comment)
        return ResponseEntity.ok(review.toResponse())
    }

    override fun patchReview(isbn: String, reviewId: Long, body: ReviewPatchRequest): ResponseEntity<ReviewResponse> {
        val review = service.patchReview(isbn, reviewId, body.rating, body.comment)
        return ResponseEntity.ok(review.toResponse())
    }

    override fun deleteReview(isbn: String, reviewId: Long): ResponseEntity<Void> {
        service.deleteReview(isbn, reviewId)
        return ResponseEntity.noContent().build()
    }
}

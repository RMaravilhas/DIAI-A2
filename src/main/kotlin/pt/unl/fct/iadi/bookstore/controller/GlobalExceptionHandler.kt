package pt.unl.fct.iadi.bookstore.controller

import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import pt.unl.fct.iadi.bookstore.controller.dto.ErrorResponse
import pt.unl.fct.iadi.bookstore.service.BookAlreadyExistsException
import pt.unl.fct.iadi.bookstore.service.BookNotFoundException
import pt.unl.fct.iadi.bookstore.service.ReviewNotFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    // ─── 404 Book not found (with i18n for US3) ───────────────────────────────

    @ExceptionHandler(BookNotFoundException::class)
    fun handleBookNotFound(ex: BookNotFoundException, response: HttpServletResponse): ResponseEntity<ErrorResponse> {
        val lang = ex.language
        val message = when {
            lang.startsWith("pt") -> "Livro com ISBN '${ex.isbn}' não encontrado"
            else -> "Book with ISBN '${ex.isbn}' not found"
        }
        response.setHeader("Content-Language", lang)
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .header("Content-Language", lang)
            .body(ErrorResponse("NOT_FOUND", message))
    }

    // ─── 404 Review not found ─────────────────────────────────────────────────

    @ExceptionHandler(ReviewNotFoundException::class)
    fun handleReviewNotFound(ex: ReviewNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse("NOT_FOUND", "Review with id '${ex.reviewId}' not found"))

    // ─── 409 Conflict ────────────────────────────────────────────────────────

    @ExceptionHandler(BookAlreadyExistsException::class)
    fun handleBookAlreadyExists(ex: BookAlreadyExistsException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse("CONFLICT", "Book with ISBN '${ex.isbn}' already exists"))

    // ─── 400 Validation ──────────────────────────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val details = ex.bindingResult.fieldErrors.joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse("VALIDATION_ERROR", details))
    }

    // ─── 400 Malformed JSON ──────────────────────────────────────────────────

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse("BAD_REQUEST", "Malformed or missing JSON request body"))
}

package pt.unl.fct.iadi.bookstore.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import pt.unl.fct.iadi.bookstore.controller.dto.BookCreateRequest
import pt.unl.fct.iadi.bookstore.controller.dto.BookPatchRequest
import pt.unl.fct.iadi.bookstore.controller.dto.BookResponse
import pt.unl.fct.iadi.bookstore.controller.dto.ErrorResponse
import pt.unl.fct.iadi.bookstore.controller.dto.ReviewCreateRequest
import pt.unl.fct.iadi.bookstore.controller.dto.ReviewPatchRequest
import pt.unl.fct.iadi.bookstore.controller.dto.ReviewResponse

@Tag(name = "Books", description = "Operations on the book catalog")
@RequestMapping("/books", produces = ["application/json"])
interface BookstoreAPI {

    // ─── Book endpoints ───────────────────────────────────────────────────────

    @Operation(summary = "List all books", description = "Returns a JSON array of all books in the catalog.")
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Successful — list of books (possibly empty)",
            content = [Content(array = ArraySchema(schema = Schema(implementation = BookResponse::class)))]
        )
    )
    @GetMapping
    fun listBooks(): ResponseEntity<List<BookResponse>>

    @Operation(summary = "Create a book", description = "Registers a new book in the catalog.")
    @ApiResponses(
        ApiResponse(
            responseCode = "201", description = "Book created — see Location header for the new resource URL",
            headers = [Header(name = "Location", description = "URL of the created book")]
        ),
        ApiResponse(
            responseCode = "409", description = "A book with this ISBN already exists",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        ),
        ApiResponse(
            responseCode = "400", description = "Validation error",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @PostMapping(consumes = ["application/json"])
    fun createBook(@Valid @RequestBody body: BookCreateRequest): ResponseEntity<Void>

    @Operation(summary = "Get a book by ISBN")
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Book found",
            content = [Content(schema = Schema(implementation = BookResponse::class))]
        ),
        ApiResponse(
            responseCode = "404", description = "Book not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @GetMapping("/{isbn}")
    fun getBook(
        @Parameter(description = "ISBN of the book", required = true)
        @PathVariable isbn: String,
        @Parameter(description = "Preferred response language for error messages (en, pt)", example = "en")
        @RequestHeader(value = "Accept-Language", defaultValue = "en") language: String
    ): ResponseEntity<BookResponse>

    @Operation(
        summary = "Replace (or create) a book",
        description = "Fully replaces a book. If the book does not exist it is created (upsert)."
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Book replaced",
            content = [Content(schema = Schema(implementation = BookResponse::class))]
        ),
        ApiResponse(
            responseCode = "201", description = "Book created (upsert)",
            headers = [Header(name = "Location", description = "URL of the new book")],
            content = [Content(schema = Schema(implementation = BookResponse::class))]
        ),
        ApiResponse(
            responseCode = "400", description = "Validation error",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @PutMapping("/{isbn}", consumes = ["application/json"])
    fun replaceBook(
        @Parameter(description = "ISBN of the book", required = true)
        @PathVariable isbn: String,
        @Valid @RequestBody body: BookCreateRequest
    ): ResponseEntity<BookResponse>

    @Operation(summary = "Partially update a book", description = "Updates only the provided fields.")
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Book updated",
            content = [Content(schema = Schema(implementation = BookResponse::class))]
        ),
        ApiResponse(
            responseCode = "404", description = "Book not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        ),
        ApiResponse(
            responseCode = "400", description = "Validation error",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @PatchMapping("/{isbn}", consumes = ["application/json"])
    fun patchBook(
        @Parameter(description = "ISBN of the book", required = true)
        @PathVariable isbn: String,
        @Valid @RequestBody body: BookPatchRequest
    ): ResponseEntity<BookResponse>

    @Operation(summary = "Delete a book", description = "Removes the book and all its reviews.")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Book deleted"),
        ApiResponse(
            responseCode = "404", description = "Book not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @DeleteMapping("/{isbn}")
    fun deleteBook(
        @Parameter(description = "ISBN of the book", required = true)
        @PathVariable isbn: String
    ): ResponseEntity<Void>

    // ─── Review endpoints ─────────────────────────────────────────────────────

    @Tag(name = "Reviews", description = "Operations on book reviews")
    @Operation(summary = "List reviews for a book")
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "List of reviews (possibly empty)",
            content = [Content(array = ArraySchema(schema = Schema(implementation = ReviewResponse::class)))]
        ),
        ApiResponse(
            responseCode = "404", description = "Book not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @GetMapping("/{isbn}/reviews")
    fun listReviews(
        @Parameter(description = "ISBN of the book", required = true)
        @PathVariable isbn: String
    ): ResponseEntity<List<ReviewResponse>>

    @Tag(name = "Reviews")
    @Operation(summary = "Add a review to a book")
    @ApiResponses(
        ApiResponse(
            responseCode = "201", description = "Review created — see Location header for the new resource URL",
            headers = [Header(name = "Location", description = "URL of the created review")]
        ),
        ApiResponse(
            responseCode = "404", description = "Book not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        ),
        ApiResponse(
            responseCode = "400", description = "Validation error",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @PostMapping("/{isbn}/reviews", consumes = ["application/json"])
    fun createReview(
        @Parameter(description = "ISBN of the book", required = true)
        @PathVariable isbn: String,
        @Valid @RequestBody body: ReviewCreateRequest
    ): ResponseEntity<Void>

    @Tag(name = "Reviews")
    @Operation(summary = "Replace a review")
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Review replaced",
            content = [Content(schema = Schema(implementation = ReviewResponse::class))]
        ),
        ApiResponse(
            responseCode = "404", description = "Book or review not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        ),
        ApiResponse(
            responseCode = "400", description = "Validation error",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @PutMapping("/{isbn}/reviews/{reviewId}", consumes = ["application/json"])
    fun replaceReview(
        @Parameter(description = "ISBN of the book", required = true)
        @PathVariable isbn: String,
        @Parameter(description = "Review ID", required = true)
        @PathVariable reviewId: Long,
        @Valid @RequestBody body: ReviewCreateRequest
    ): ResponseEntity<ReviewResponse>

    @Tag(name = "Reviews")
    @Operation(summary = "Partially update a review")
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Review updated",
            content = [Content(schema = Schema(implementation = ReviewResponse::class))]
        ),
        ApiResponse(
            responseCode = "404", description = "Book or review not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        ),
        ApiResponse(
            responseCode = "400", description = "Validation error",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @PatchMapping("/{isbn}/reviews/{reviewId}", consumes = ["application/json"])
    fun patchReview(
        @Parameter(description = "ISBN of the book", required = true)
        @PathVariable isbn: String,
        @Parameter(description = "Review ID", required = true)
        @PathVariable reviewId: Long,
        @Valid @RequestBody body: ReviewPatchRequest
    ): ResponseEntity<ReviewResponse>

    @Tag(name = "Reviews")
    @Operation(summary = "Delete a review")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Review deleted"),
        ApiResponse(
            responseCode = "404", description = "Book or review not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @DeleteMapping("/{isbn}/reviews/{reviewId}")
    fun deleteReview(
        @Parameter(description = "ISBN of the book", required = true)
        @PathVariable isbn: String,
        @Parameter(description = "Review ID", required = true)
        @PathVariable reviewId: Long
    ): ResponseEntity<Void>
}

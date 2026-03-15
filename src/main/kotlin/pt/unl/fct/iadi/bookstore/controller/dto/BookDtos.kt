package pt.unl.fct.iadi.bookstore.controller.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

// ─── Requests ────────────────────────────────────────────────────────────────

@Schema(description = "Payload for creating a new book")
data class BookCreateRequest(

    @field:NotBlank(message = "ISBN must not be blank")
    @Schema(description = "Unique ISBN identifier", example = "9780134685991")
    val isbn: String,

    @field:NotBlank(message = "Title must not be blank")
    @field:Size(min = 1, max = 120, message = "Title must be between 1 and 120 characters")
    @Schema(description = "Book title", example = "Effective Java", minLength = 1, maxLength = 120)
    val title: String,

    @field:NotBlank(message = "Author must not be blank")
    @field:Size(min = 1, max = 80, message = "Author must be between 1 and 80 characters")
    @Schema(description = "Book author", example = "Joshua Bloch", minLength = 1, maxLength = 80)
    val author: String,

    @field:DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Schema(description = "Price in euros", example = "29.99", minimum = "0.01")
    val price: Double,

    @field:NotBlank(message = "Image URL must not be blank")
    @field:Pattern(
        regexp = "^https?://.*",
        message = "Image must be a valid URL starting with http:// or https://"
    )
    @Schema(description = "URL of the book cover image", example = "https://example.com/cover.jpg")
    val image: String
)

@Schema(description = "Payload for partially updating a book. Only provided fields are applied.")
data class BookPatchRequest(

    @field:Size(min = 1, max = 120, message = "Title must be between 1 and 120 characters")
    @Schema(description = "New title (optional)", example = "Effective Java 4th Edition", nullable = true)
    val title: String? = null,

    @field:Size(min = 1, max = 80, message = "Author must be between 1 and 80 characters")
    @Schema(description = "New author (optional)", example = "Joshua Bloch", nullable = true)
    val author: String? = null,

    @field:DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Schema(description = "New price (optional)", example = "24.99", nullable = true, minimum = "0.01")
    val price: Double? = null,

    @field:Pattern(
        regexp = "^https?://.*",
        message = "Image must be a valid URL starting with http:// or https://"
    )
    @Schema(description = "New image URL (optional)", nullable = true)
    val image: String? = null
)

// ─── Response ────────────────────────────────────────────────────────────────

@Schema(description = "Book resource as returned by the API")
data class BookResponse(
    @Schema(description = "Unique ISBN identifier", example = "9780134685991")
    val isbn: String,

    @Schema(description = "Book title", example = "Effective Java")
    val title: String,

    @Schema(description = "Book author", example = "Joshua Bloch")
    val author: String,

    @Schema(description = "Price in euros", example = "29.99", minimum = "0.01")
    val price: Double,

    @Schema(description = "URL of the book cover image", example = "https://example.com/cover.jpg")
    val image: String
)

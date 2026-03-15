package pt.unl.fct.iadi.bookstore.controller.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size

// ─── Requests ────────────────────────────────────────────────────────────────

@Schema(description = "Payload for creating or replacing a review")
data class ReviewCreateRequest(

    @field:Min(value = 1, message = "Rating must be at least 1")
    @field:Max(value = 5, message = "Rating must be at most 5")
    @Schema(description = "Rating from 1 to 5", example = "4", minimum = "1", maximum = "5")
    val rating: Int,

    @field:Size(max = 500, message = "Comment must be at most 500 characters")
    @Schema(description = "Optional comment (max 500 characters)", example = "Great read!", nullable = true, maxLength = 500)
    val comment: String? = null
)

@Schema(description = "Payload for partially updating a review. Only provided fields are applied.")
data class ReviewPatchRequest(

    @field:Min(value = 1, message = "Rating must be at least 1")
    @field:Max(value = 5, message = "Rating must be at most 5")
    @Schema(description = "New rating (optional, 1–5)", nullable = true)
    val rating: Int? = null,

    @field:Size(max = 500, message = "Comment must be at most 500 characters")
    @Schema(description = "New comment (optional)", nullable = true)
    val comment: String? = null
)

// ─── Response ────────────────────────────────────────────────────────────────

@Schema(description = "Review resource as returned by the API")
data class ReviewResponse(
    @Schema(description = "Unique review identifier", example = "1")
    val id: Long,

    @Schema(description = "Rating from 1 to 5", example = "4")
    val rating: Int,

    @Schema(description = "Optional comment", example = "Great read!", nullable = true)
    val comment: String?
)

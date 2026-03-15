package pt.unl.fct.iadi.bookstore.controller.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Standard error response payload")
data class ErrorResponse(
    @Schema(description = "Machine-readable error identifier", example = "NOT_FOUND")
    val error: String,

    @Schema(description = "Human-readable error description", example = "Book with ISBN 9780134685991 not found")
    val message: String
)

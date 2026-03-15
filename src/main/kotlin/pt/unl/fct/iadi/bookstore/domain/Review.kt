package pt.unl.fct.iadi.bookstore.domain

data class Review(
    val id: Long,
    val bookIsbn: String,
    var rating: Int,
    var comment: String?
)

package pt.unl.fct.iadi.bookstore.domain

data class Book(
    val isbn: String,
    var title: String,
    var author: String,
    var price: Double,
    var image: String
)

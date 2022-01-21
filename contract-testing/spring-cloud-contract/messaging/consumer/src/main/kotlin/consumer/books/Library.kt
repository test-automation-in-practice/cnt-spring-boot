package consumer.books

// This is just an interface because we don't need an actual implementation for demonstrating the consumer-side of
// Spring-based messaging contract testing.

interface Library {
    fun add(book: Book)
}

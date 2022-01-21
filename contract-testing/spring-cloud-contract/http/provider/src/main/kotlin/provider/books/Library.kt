package provider.books

import java.util.UUID

// This is just an interface because we don't need an actual implementation for demonstrating the provider-side of
// PACT-based contract testing.

interface Library {
    fun findById(id: UUID): BookRecord?
}

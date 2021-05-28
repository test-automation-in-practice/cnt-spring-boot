package provider.books

import java.util.*

interface BookDataStore {
    fun getById(id: UUID): BookRecord?
}
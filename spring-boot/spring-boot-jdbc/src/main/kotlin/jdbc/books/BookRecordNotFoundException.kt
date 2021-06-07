package jdbc.books

import java.util.UUID

class BookRecordNotFoundException(id: UUID) :
    RuntimeException("The book record with ID [$id] was not found in the database!")

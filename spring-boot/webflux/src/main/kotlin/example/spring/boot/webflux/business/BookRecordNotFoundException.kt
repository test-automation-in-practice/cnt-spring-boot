package example.spring.boot.webflux.business

import java.util.UUID

class BookRecordNotFoundException(val id: UUID) : RuntimeException("Book [$id] not found!")

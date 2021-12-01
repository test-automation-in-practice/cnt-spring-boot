package web.business

import java.util.*

class BookRecordNotFoundException(val id: UUID) : RuntimeException("Book [$id] not found!")

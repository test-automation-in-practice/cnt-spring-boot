package springsecurity.domain

import springsecurity.domain.model.BookRecord
import java.util.*

object BookRecordExamples {
    val REFACTORING = BookRecord(
        id = UUID.fromString("cd690768-74d4-48a8-8443-664975dd46b5"),
        book = BookExamples.REFACTORING
    )
}

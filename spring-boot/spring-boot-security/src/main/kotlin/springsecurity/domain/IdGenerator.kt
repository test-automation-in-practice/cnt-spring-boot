package springsecurity.domain

import java.util.UUID

interface IdGenerator {
    fun generate(): UUID
}

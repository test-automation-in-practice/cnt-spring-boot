package springsecurity.common

import org.springframework.stereotype.Component
import springsecurity.domain.IdGenerator
import java.util.UUID

@Component
class RandomIdGenerator : IdGenerator {
    override fun generate(): UUID = UUID.randomUUID()
}

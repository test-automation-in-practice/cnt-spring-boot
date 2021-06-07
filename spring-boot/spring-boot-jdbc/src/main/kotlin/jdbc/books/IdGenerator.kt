package jdbc.books

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class IdGenerator {

    fun generateId(): UUID {
        return UUID.randomUUID()
    }

}

package jdbc.books

import org.springframework.stereotype.Component
import java.util.*

@Component
class IdGenerator {

    fun generateId(): UUID {
        return UUID.randomUUID()
    }

}
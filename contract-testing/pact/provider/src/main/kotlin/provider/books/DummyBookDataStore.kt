package provider.books

import org.springframework.stereotype.Service
import java.util.*

@Service
class DummyBookDataStore : BookDataStore {
    override fun getById(id: UUID): BookRecord? = null
}
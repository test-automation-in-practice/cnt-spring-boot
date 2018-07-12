package mongodb.foo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@DataMongoTest
@ExtendWith(SpringExtension::class)
internal class FooRepositoryTest {

    @Autowired lateinit var cut: FooRepository

    @Test fun `entity can be saved`() {
        val id = UUID.randomUUID()
        val entity = FooDocument(id, "bar", 42)
        val savedEntity = cut.save(entity)
        assertThat(savedEntity).isEqualTo(entity)
    }

    @Test fun `entity can be found by id`() {
        val id = UUID.randomUUID()
        val savedEntity = cut.save(FooDocument(id, "bar", 42))
        val foundEntity = cut.findById(id)
        assertThat(foundEntity).hasValue(savedEntity)
    }

    @Test fun `entity can be found by bar`() {
        val e1 = cut.save(FooDocument(UUID.randomUUID(), "bar#1", 42))
        val e2 = cut.save(FooDocument(UUID.randomUUID(), "bar#1", 43))
        val e3 = cut.save(FooDocument(UUID.randomUUID(), "bar#2", 44))
        val foundEntities = cut.findByBar("bar#1")
        assertThat(foundEntities)
                .contains(e1, e2)
                .doesNotContain(e3)
    }

}
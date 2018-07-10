package jpa.foo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@DataJpaTest
@ExtendWith(SpringExtension::class)
internal class FooRepositoryTest {

    @Autowired lateinit var cut: FooRepository

    @Test fun `entity can be saved`() {
        val id = UUID.randomUUID()
        val entity = FooEntity(id, "bar", 42)
        val savedEntity = cut.save(entity)
        assertThat(savedEntity).isEqualTo(entity)
    }

    @Test fun `entity can be found by id`() {
        val id = UUID.randomUUID()
        val savedEntity = cut.save(FooEntity(id, "bar", 42))
        val foundEntity = cut.findById(id)
        assertThat(foundEntity).hasValue(savedEntity)
    }

}
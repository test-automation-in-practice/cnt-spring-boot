package jdbc.foo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@JdbcTest
@ExtendWith(SpringExtension::class)
internal class FooRepositoryTest(
        @Autowired private val jdbcTemplate: NamedParameterJdbcTemplate
) {

    private val idGenerator = mock(IdGenerator::class.java)
    private val cut = FooRepository(jdbcTemplate, idGenerator)

    @Test fun `creating a foo returns a persisted instance`() {
        val fixedId = UUID.randomUUID()

        given(idGenerator.generateId()).willReturn(fixedId)

        val persistedFoo = cut.create(Foo("bar", 42))
        with(persistedFoo) {
            assertThat(id).isEqualTo(fixedId)
            assertThat(foo.bar).isEqualTo("bar")
            assertThat(foo.xur).isEqualTo(42)
        }
    }

    @Test fun `duplicated keys during creation are handled`() {
        val fixedId1 = UUID.randomUUID()
        val fixedId2 = UUID.randomUUID()

        given(idGenerator.generateId()).willReturn(fixedId1, fixedId1, fixedId2)

        with(cut.create(Foo("bar", 42))) {
            assertThat(id).isEqualTo(fixedId1)
        }

        with(cut.create(Foo("rab", 24))) {
            assertThat(id).isEqualTo(fixedId2)
        }

        verify(idGenerator, times(3)).generateId()
    }

    @Test fun `updating an existing foo changes all its data except the id`() {
        val fixedId = UUID.randomUUID()
        given(idGenerator.generateId()).willReturn(fixedId)

        val createdFoo = cut.create(Foo("bar", 42))

        with(cut.findBy(createdFoo.id)!!) {
            assertThat(id).isEqualTo(fixedId)
            assertThat(foo.bar).isEqualTo("bar")
            assertThat(foo.xur).isEqualTo(42)
        }

        cut.update(createdFoo.copy(foo = Foo("rab", 24)))

        with(cut.findBy(createdFoo.id)!!) {
            assertThat(id).isEqualTo(fixedId)
            assertThat(foo.bar).isEqualTo("rab")
            assertThat(foo.xur).isEqualTo(24)
        }
    }

    @Test fun `updating non existing foo throws exception`() {
        val persistedFoo = PersistedFoo(UUID.randomUUID(), Foo("bar", 42))

        assertThrows<FooNotFoundException> {
            cut.update(persistedFoo)
        }
    }

    @Test fun `existing foos can be found by id`() {
        given(idGenerator.generateId()).willReturn(UUID.randomUUID())

        val persistedFoo = cut.create(Foo("bar", 42))
        val foundFoo = cut.findBy(persistedFoo.id)

        assertThat(foundFoo).isEqualTo(persistedFoo)
    }

    @Test fun `non existing foos are returned as null when trying to find them by id`() {
        assertThat(cut.findBy(UUID.randomUUID())).isNull()
    }

    @Test fun `existing foos can be deleted by id`() {
        given(idGenerator.generateId()).willReturn(UUID.randomUUID())

        val persistedFoo = cut.create(Foo("bar", 42))
        assertThat(cut.findBy(persistedFoo.id)).isNotNull()

        cut.deleteBy(persistedFoo.id)
        assertThat(cut.findBy(persistedFoo.id)).isNull()
    }

    @Test fun `deleting non existing foos throws exception`() {
        assertThrows<FooNotFoundException> {
            cut.deleteBy(UUID.randomUUID())
        }
    }

}
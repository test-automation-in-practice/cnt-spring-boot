package example.spring.boot.data.redis.persistence

import example.spring.boot.data.redis.config.RedisRepositoryConfiguration
import example.spring.boot.data.redis.utils.RunWithDockerizedRedis
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert.assertEquals
import org.skyscreamer.jsonassert.JSONCompareMode.LENIENT
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.test.context.ActiveProfiles
import java.util.UUID.randomUUID

@RunWithDockerizedRedis
@DataRedisTest
@ActiveProfiles("test")
@Import(RedisRepositoryConfiguration::class)
internal class BookRecordRepositoryTest(
    @Autowired val cut: BookRecordRepository
) {

    @BeforeEach
    fun clearRepository() {
        cut.deleteAll()
    }

    @Test
    fun `document can be saved`() {
        val entity = bookRecordDocument()
        val savedEntity = cut.save(entity)
        assertThat(savedEntity).isEqualTo(entity)
    }

    @Test
    fun `document can be found by id`() {
        val savedEntity = cut.save(bookRecordDocument())
        val foundEntity = cut.findById(savedEntity.id)
        assertThat(foundEntity).hasValue(savedEntity)
    }

    @Test
    fun `document can be found by title`() {
        val e1 = cut.save(bookRecordDocument("Clean Code"))
        val e2 = cut.save(bookRecordDocument("Clean Architecture"))
        val e3 = cut.save(bookRecordDocument("Clean Code"))

        val foundEntities = cut.findByTitle("Clean Code")

        assertThat(foundEntities)
            .contains(e1, e3)
            .doesNotContain(e2)
    }

    /*
        Structure inside Redis:

        HASH: "BookRecord:f3d35180-8c6c-4e43-abfd-05db5371790e"
            -> actual BookRecord as JSON in the "_raw" hash key
        SET: "BookRecord:f3d35180-8c6c-4e43-abfd-05db5371790e:idx"
            -> names of indices this BookRecord is referenced in
        SET: "BookRecord:title:Clean Code"
            -> set of all BookRecord (IDs) with the title "Clean Code"
        SET: "BookRecord"
            -> set of all BookRecord IDs
     */
    @Test
    fun `data is stored as JSON`(@Autowired connectionFactory: RedisConnectionFactory) {
        val template = redisTemplate(connectionFactory)
        val saved = cut.save(bookRecordDocument("JSON for Beginners"))

        val key = "BookRecord:${saved.id}"
        val hashKey = "_raw"

        assertEquals(
            /* expectedStr = */ """{ "title": "JSON for Beginners" }""",
            /* actualStr = */ template.opsForHash<String, String>().get(key, hashKey),
            /* compareMode = */ LENIENT
        )
    }

    private fun bookRecordDocument(title: String = "Clean Code") =
        BookRecord(randomUUID(), title, "9780123456789")

    private fun redisTemplate(connectionFactory: RedisConnectionFactory) =
        RedisTemplate<String, String>()
            .apply {
                setConnectionFactory(connectionFactory)
                setDefaultSerializer(StringRedisSerializer())
                afterPropertiesSet()
            }

}

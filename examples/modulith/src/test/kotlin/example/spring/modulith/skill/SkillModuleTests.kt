package example.spring.modulith.skill

import example.spring.modulith.skill.internal.SkillRepresentation
import example.spring.modulith.utils.InitializeWithContainers
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.MethodOrderer.MethodName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.modulith.test.ApplicationModuleTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.test.web.servlet.client.RestTestClient.RequestHeadersSpec
import java.util.UUID
import kotlin.reflect.KClass

@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@InitializeWithContainers
@AutoConfigureRestTestClient
@TestMethodOrder(MethodName::class)
@ApplicationModuleTest(webEnvironment = RANDOM_PORT)
class SkillModuleTests(
    @Autowired private val testClient: RestTestClient
) {

    private lateinit var skillId: UUID

    @Test
    fun `01 create skill`() {
        val skill = executeAndReturn<SkillRepresentation> {
            post()
                .uri("/api/skills")
                .contentType(APPLICATION_JSON)
                .body("""{ "label": "Kotlin" }""")
        }

        with(skill!!) {
            label shouldBe "Kotlin"
        }

        skillId = skill.id
    }

    @Test
    fun `02 get skill`() {
        val skill = executeAndReturn<SkillRepresentation> {
            get().uri("/api/skills/$skillId")
        }

        with(skill!!) {
            label shouldBe "Kotlin"
        }
    }

    @Test
    fun `03 update skill`() {
        val skill = executeAndReturn<SkillRepresentation> {
            put()
                .uri("/api/skills/$skillId")
                .contentType(APPLICATION_JSON)
                .body("""{ "label": "Kotlin (JVM)" }""")
        }

        with(skill!!) {
            label shouldBe "Kotlin (JVM)"
        }
    }

    @Test
    fun `04 delete skill`() {
        val skill = executeAndReturn<SkillRepresentation> {
            delete().uri("/api/skills/$skillId")
        }

        skill shouldBe null
    }

    @Test
    fun `05 get deleted skill`() {
        val skill = executeAndReturn<SkillRepresentation> {
            get().uri("/api/skills/$skillId")
        }

        skill shouldBe null
    }

    private inline fun <reified T : Any> executeAndReturn(block: RestTestClient.() -> RequestHeadersSpec<*>): T? =
        executeAndReturn(T::class, block)

    private inline fun <T : Any> executeAndReturn(
        type: KClass<T>,
        block: RestTestClient.() -> RequestHeadersSpec<*>
    ): T? = block(testClient)
        .exchange()
        .expectStatus().is2xxSuccessful()
        .returnResult(type.java)
        .getResponseBody()

}

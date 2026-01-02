package example.spring.modulith.employee

import example.spring.modulith.employee.internal.EmployeeRepresentation
import example.spring.modulith.employee.internal.KnowledgeRepresentation
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
import tools.jackson.databind.JsonNode
import java.lang.Thread.sleep
import java.util.UUID
import kotlin.reflect.KClass

@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@InitializeWithContainers
@AutoConfigureRestTestClient
@TestMethodOrder(MethodName::class)
@ApplicationModuleTest(extraIncludes = ["skill"], webEnvironment = RANDOM_PORT)
class EmployeeModuleTests(
    @Autowired private val testClient: RestTestClient
) {

    private lateinit var employeeId: UUID
    private lateinit var skillId: UUID

    @Test
    fun `01 create employee`() {
        val employee = executeAndReturn<EmployeeRepresentation> {
            post()
                .uri("/api/employees")
                .contentType(APPLICATION_JSON)
                .body(
                    """
                    {
                      "firstName": "John",
                      "lastName": "Doe"
                    }
                    """
                )
        }

        with(employee!!) {
            firstName shouldBe "John"
            lastName shouldBe "Doe"
            knowledge shouldBe emptyList()
        }

        employeeId = employee.id
    }

    @Test
    fun `02 get employee`() {
        val employee = executeAndReturn<EmployeeRepresentation> {
            get().uri("/api/employees/$employeeId")
        }

        with(employee!!) {
            firstName shouldBe "John"
            lastName shouldBe "Doe"
            knowledge shouldBe emptyList()
        }
    }

    @Test
    fun `03 update employee`() {
        val employee = executeAndReturn<EmployeeRepresentation> {
            put()
                .uri("/api/employees/$employeeId")
                .contentType(APPLICATION_JSON)
                .body(
                    """
                    {
                      "firstName": "Johnathon",
                      "lastName": "Doer"
                    }
                    """
                )
        }

        with(employee!!) {
            firstName shouldBe "Johnathon"
            lastName shouldBe "Doer"
            knowledge shouldBe emptyList()
        }
    }

    @Test
    fun `04-1 create skill`() {
        val skill = executeAndReturn<JsonNode> {
            post()
                .uri("/api/skills")
                .contentType(APPLICATION_JSON)
                .body("""{ "label": "Kotlin" }""")
        }

        skillId = skill!!["id"].asString().let(UUID::fromString)
    }

    @Test
    fun `04-2 set employee knowledge`() {
        val employee = executeAndReturn<EmployeeRepresentation> {
            put()
                .uri("/api/employees/$employeeId/knowledge/$skillId")
                .contentType(APPLICATION_JSON)
                .body("""{ "level": 10 }""")
        }

        with(employee!!) {
            knowledge shouldBe listOf(KnowledgeRepresentation(skillId, "Kotlin", 10))
        }
    }

    @Test
    fun `05-1 change skill`() {
        execute {
            put()
                .uri("/api/skills/$skillId")
                .contentType(APPLICATION_JSON)
                .body("""{ "label": "Kotlin (JVM)" }""")
        }
    }

    @Test
    fun `05-2 get updated knowledge`() {
        sleep(100) // update needs time to propagate
        val employee = executeAndReturn<EmployeeRepresentation> {
            get().uri("/api/employees/$employeeId")
        }

        with(employee!!) {
            knowledge shouldBe listOf(KnowledgeRepresentation(skillId, "Kotlin (JVM)", 10))
        }
    }

    @Test
    fun `06 delete employee knowledge`() {
        val employee = executeAndReturn<EmployeeRepresentation> {
            delete().uri("/api/employees/$employeeId/knowledge/$skillId")
        }

        with(employee!!) {
            knowledge shouldBe emptyList()
        }
    }

    @Test
    fun `07 delete employee`() {
        val employee = executeAndReturn<EmployeeRepresentation> {
            delete().uri("/api/employees/$employeeId")
        }

        employee shouldBe null
    }

    @Test
    fun `08 get deleted employee`() {
        val employee = executeAndReturn<EmployeeRepresentation> {
            get().uri("/api/employees/$employeeId")
        }

        employee shouldBe null
    }

    private inline fun <reified T : Any> executeAndReturn(block: RestTestClient.() -> RequestHeadersSpec<*>): T? =
        executeAndReturn(T::class, block)

    private inline fun <T : Any> executeAndReturn(
        type: KClass<T>,
        block: RestTestClient.() -> RequestHeadersSpec<*>
    ): T? = execute(block)
        .returnResult(type.java)
        .getResponseBody()

    private inline fun execute(block: RestTestClient.() -> RequestHeadersSpec<*>) =
        block(testClient)
            .exchange()
            .expectStatus().is2xxSuccessful()

}

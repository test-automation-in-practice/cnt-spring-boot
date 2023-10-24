package example.spring.modulith.employee.internal

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.UUID

@Document("employees")
data class Employee(
    @Id val id: UUID,
    val data: Data,
    val knowledge: List<Knowledge>,
    val created: Instant,
    val lastModified: Instant,
) {
    data class Data(
        val firstName: String,
        val lastName: String,
    )
}

data class Knowledge(
    val skillId: UUID,
    val label: String,
    val level: Int,
)

data class EmployeeRepresentation(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val knowledge: List<KnowledgeRepresentation>
)

data class KnowledgeRepresentation(
    val id: UUID,
    val label: String,
    val level: Int,
)

fun Employee.toRepresentation(): EmployeeRepresentation =
    EmployeeRepresentation(
        id = id,
        firstName = data.firstName,
        lastName = data.lastName,
        knowledge = knowledge.map { it.toRepresentation() }
    )

fun Knowledge.toRepresentation(): KnowledgeRepresentation =
    KnowledgeRepresentation(
        id = skillId,
        label = label,
        level = level
    )

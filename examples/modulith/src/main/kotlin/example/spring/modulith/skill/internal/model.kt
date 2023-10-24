package example.spring.modulith.skill.internal

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.UUID

@Document("skills")
data class Skill(
    @Id val id: UUID,
    val data: Data,
    val created: Instant,
    val lastModified: Instant
) {
    data class Data(
        val label: String,
    )
}

data class SkillRepresentation(
    val id: UUID,
    val label: String,
)

fun Skill.toRepresentation(): SkillRepresentation =
    SkillRepresentation(
        id = id,
        label = data.label,
    )

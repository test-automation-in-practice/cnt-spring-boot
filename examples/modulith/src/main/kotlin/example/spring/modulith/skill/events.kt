package example.spring.modulith.skill

import org.jmolecules.event.types.DomainEvent
import org.springframework.modulith.events.Externalized
import java.time.Instant
import java.util.UUID

@Externalized("skill-events::SkillCreated")
data class SkillCreated(
    override val id: UUID,
    override val timestamp: Instant,
    val skill: SkillDto,
) : SkillEvent

@Externalized("skill-events::SkillDataUpdated")
data class SkillDataUpdated(
    override val id: UUID,
    override val timestamp: Instant,
    val oldSkill: SkillDto,
    val newSkill: SkillDto,
) : SkillEvent {
    init {
        require(oldSkill.id == newSkill.id) {
            "The 'old' and 'new' instance need to reference the same skill!"
        }
    }
}

@Externalized("skill-events::SkillDeleted")
data class SkillDeleted(
    override val id: UUID,
    override val timestamp: Instant,
    val skillId: UUID,
) : SkillEvent

interface SkillEvent : DomainEvent {
    val id: UUID
    val timestamp: Instant
}

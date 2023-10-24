package example.spring.modulith.skill.internal

import example.spring.modulith.skill.SkillCreated
import example.spring.modulith.skill.SkillDataUpdated
import example.spring.modulith.skill.SkillDeleted
import example.spring.modulith.skill.toDto
import org.jmolecules.event.types.DomainEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.util.IdGenerator
import java.time.Clock
import java.time.Instant
import java.util.UUID

@Component
class SkillEventPublisher(
    private val publisher: ApplicationEventPublisher,
    private val idGenerator: IdGenerator,
    private val clock: Clock
) {

    fun publishSkillCreated(skill: Skill) =
        publish(SkillCreated(id(), now(), skill.toDto()))

    fun publishSkillDataUpdated(old: Skill, new: Skill) =
        publish(SkillDataUpdated(id(), now(), old.toDto(), new.toDto()))

    fun publishSkillDeleted(id: UUID) =
        publish(SkillDeleted(id(), now(), id))

    private fun id(): UUID = idGenerator.generateId()
    private fun now(): Instant = clock.instant()
    private fun publish(event: DomainEvent) = publisher.publishEvent(event)

}

package example.spring.modulith.skill.internal

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.IdGenerator
import java.time.Clock
import java.util.UUID

@Service
@Transactional
class SkillService(
    private val repository: SkillRepository,
    private val publisher: SkillEventPublisher,
    private val idGenerator: IdGenerator,
    private val clock: Clock
) {

    fun get(id: UUID): Skill? =
        repository.findByIdOrNull(id)

    fun create(data: Skill.Data): Skill {
        val id = idGenerator.generateId()
        val now = clock.instant()

        val skill = Skill(
            id = id,
            data = data,
            created = now,
            lastModified = now
        )

        repository.save(skill)
        publisher.publishSkillCreated(skill)

        return skill
    }

    fun updateData(id: UUID, block: (Skill.Data) -> Skill.Data): Skill? {
        var oldSkill: Skill? = null

        val updatedSkill = update(id) { skill ->
            oldSkill = skill
            skill.copy(data = block(skill.data))
        }

        if (oldSkill != null && updatedSkill != null) {
            publisher.publishSkillDataUpdated(oldSkill!!, updatedSkill)
        }

        return updatedSkill
    }

    fun update(id: UUID, block: (Skill) -> Skill): Skill? {
        val skill = get(id) ?: return null
        val updatedSkill = block(skill)
        repository.save(updatedSkill)
        return updatedSkill
    }

    fun delete(id: UUID) {
        if (repository.existsById(id)) {
            repository.deleteById(id)
            publisher.publishSkillDeleted(id)
        }
    }

}

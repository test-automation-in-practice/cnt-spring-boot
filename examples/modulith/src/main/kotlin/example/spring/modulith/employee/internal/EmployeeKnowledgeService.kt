package example.spring.modulith.employee.internal

import example.spring.modulith.skill.SkillAccessor
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class EmployeeKnowledgeService(
    private val skills: SkillAccessor,
    private val service: EmployeeService,
) {

    fun setKnowledge(employeeId: UUID, skillId: UUID, level: Int): Employee? {
        val skill = skills.getById(skillId) ?: return null
        return service.update(employeeId) { employee ->
            var skillKnowledge = employee.knowledge.firstOrNull { it.skillId == skill.id }
            val otherKnowledge = employee.knowledge.filter { it.skillId != skill.id }

            skillKnowledge = skillKnowledge?.copy(level = level) ?: Knowledge(skill.id, skill.label, level)

            employee.copy(knowledge = otherKnowledge + skillKnowledge)
        }
    }

    fun deleteKnowledge(employeeId: UUID, skillId: UUID): Employee? {
        return service.update(employeeId) { employee ->
            employee.copy(knowledge = employee.knowledge.filter { it.skillId != skillId })
        }
    }

}
